package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.eventsourcing.repo.MemEventRepo;
import io.memoria.jutils.jcore.eventsourcing.repo.R2EventRepo;
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

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class EventStoreTest {
  private static final IdGenerator idGenerator = () -> Id.of(1);
  private final String topic = "topic_" + new Random().nextInt(1000);

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
    StepVerifier.create(eventRepo.find(topic)).expectNext(expectedEvents).verifyComplete();
  }

  private EventStore<User, UserCommand> createEventStore(EventRepo eventRepo) {
    var createTopic = eventRepo.createTopic(topic);
    var createEventStore = EventStores.create(topic,
                                              new Visitor(),
                                              eventRepo,
                                              new UserDecider(idGenerator),
                                              new UserEvolver());
    return createTopic.then(createEventStore).block();
  }

  private CreateUser createCommand(Integer i) {
    return new CreateUser(idGenerator.get(), Id.of(topic), Id.of("user_" + i), "name_" + i);
  }

  private Event createEvent(Integer i) {
    return new UserCreated(idGenerator.get(), Id.of(topic), Id.of("user_" + i), "name_" + i);
  }

  private static Stream<EventRepo> eventRepo() {
    var mem = new MemEventRepo(new ConcurrentHashMap<>());
    var con = ConnectionFactories.get("r2dbc:h2:mem:///testDB");
    var r2 = new R2EventRepo(con, new SerializableTransformer());
    return Stream.of(mem, r2);
  }
}