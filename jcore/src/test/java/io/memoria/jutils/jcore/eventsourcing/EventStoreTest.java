package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.eventsourcing.repo.MemEventRepo;
import io.memoria.jutils.jcore.eventsourcing.user.User;
import io.memoria.jutils.jcore.eventsourcing.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.eventsourcing.user.UserDecider;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvolver;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class EventStoreTest {
  private static final IdGenerator idGenerator = () -> Id.of(1);
  private final EventRepo eventRepo = new MemEventRepo(new ConcurrentHashMap<>());
  private final EventStore<User, UserCommand> eventStore;
  private final String agg;

  EventStoreTest() {
    // Setup
    agg = "Agg_" + new Random().nextInt(1000);
    eventStore = EventStores.create(agg, new Visitor(), eventRepo, new UserDecider(idGenerator), new UserEvolver())
                            .block();
  }

  @Test
  void handleCommands() {
    // Given
    var count = 3;
    var commands = Flux.range(0, count).map(EventStoreTest::createCommand);
    var expectedEvents = List.range(0, count).map(EventStoreTest::createEvent);
    // When
    StepVerifier.create(commands.concatMap(eventStore)).expectNextCount(count).verifyComplete();
    // Then
    StepVerifier.create(eventRepo.find(agg)).expectNext(expectedEvents).verifyComplete();
  }

  private static CreateUser createCommand(Integer i) {
    return new CreateUser(idGenerator.get(), Id.of("user_" + i), "name_" + i);
  }

  private static Event createEvent(Integer i) {
    return new UserCreated(idGenerator.get(), Id.of("user_" + i), "name_" + i);
  }
}