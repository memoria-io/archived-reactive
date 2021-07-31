package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.repo.EventRepo;
import io.memoria.reactive.core.eventsourcing.repo.mem.MemESRepo;
import io.memoria.reactive.core.eventsourcing.repo.r2.R2ESAdmin;
import io.memoria.reactive.core.eventsourcing.repo.r2.R2ESRepo;
import io.memoria.reactive.core.eventsourcing.user.User.Visitor;
import io.memoria.reactive.core.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.user.UserDecider;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.user.UserEvolver;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.IdGenerator;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.r2dbc.spi.ConnectionFactories;
import io.vavr.collection.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.stream.Stream;

class EventStoreTest {
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

  private static EventStore createEventStore(EventRepo eventRepo) {
    var state = ES.buildState(eventRepo, new UserEvolver()).block();
    return new EventStore(new Visitor(), state, eventRepo, new UserDecider(idGenerator), new UserEvolver());
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