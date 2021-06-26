package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.eventsourcing.repo.MemESRepo;
import io.memoria.jutils.jcore.eventsourcing.repo.R2ESAdmin;
import io.memoria.jutils.jcore.eventsourcing.repo.R2ESRepo;
import io.memoria.jutils.jcore.eventsourcing.user.User;
import io.memoria.jutils.jcore.eventsourcing.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.eventsourcing.user.UserDecider;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvolver;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jcore.text.SerializableTransformer;
import io.r2dbc.spi.ConnectionFactories;
import io.vavr.collection.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.stream.Stream;

class ESHandlerTest {
  private static final IdGenerator idGenerator = () -> Id.of(1);

  @ParameterizedTest
  @MethodSource("eventRepo")
  void handleCommands(EventRepo eventRepo) {
    // Given
    var eventStore = createEventStore(eventRepo);
    var count = 3;
    var commands = Flux.range(0, count).map(this::createCommand);
    var expectedEvents = List.range(0, count).map(this::createEvent);
    // When
    StepVerifier.create(commands.concatMap(eventStore)).expectNextCount(count).verifyComplete();
    // Then
    StepVerifier.create(eventRepo.find()).expectNext(expectedEvents).verifyComplete();
    StepVerifier.create(eventRepo.find(Id.of("user_0"))).expectNext(List.of(expectedEvents.head())).verifyComplete();
  }

  private CreateUser createCommand(Integer i) {
    return new CreateUser(idGenerator.get(), Id.of("user_" + i), "name_" + i);
  }

  private Event createEvent(Integer i) {
    return new UserCreated(idGenerator.get(), Id.of("user_" + i), "name_" + i);
  }

  private static ESHandler<User, UserCommand> createEventStore(EventRepo eventRepo) {
    var state = ESHandler.buildState(eventRepo, new UserEvolver()).block();
    return new ESHandler<>(new Visitor(), state, eventRepo, new UserDecider(idGenerator), new UserEvolver());
  }

  private static R2ESRepo createR2ESRepo() {
    var con = ConnectionFactories.get("r2dbc:h2:mem:///testDB");
    var tableName = "USERS_TABLE";
    R2ESAdmin.createTableIfNotExists(con, tableName).block();
    return new R2ESRepo(con, tableName, new SerializableTransformer());
  }

  private static Stream<EventRepo> eventRepo() {
    return Stream.of(new MemESRepo(new ArrayList<>()), createR2ESRepo());
  }
}