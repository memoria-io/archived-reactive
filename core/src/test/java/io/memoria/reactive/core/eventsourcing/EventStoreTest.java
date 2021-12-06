package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.mem.MemEventStore;
import io.memoria.reactive.core.eventsourcing.mem.MemStateStore;
import io.memoria.reactive.core.eventsourcing.user.User.Account;
import io.memoria.reactive.core.eventsourcing.user.User.Visitor;
import io.memoria.reactive.core.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.user.UserDecider;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.user.UserEvolver;
import io.memoria.reactive.core.id.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class EventStoreTest {
  private static final ConcurrentHashMap<Id, State> stateDB = new ConcurrentHashMap<>();
  private static final List<Event> eventDB = new ArrayList<>();

  @BeforeEach
  void beforeEach() {
    stateDB.clear();
    eventDB.clear();
  }

  @Test
  void handleCommands() {
    var count = 3;
    // Given
    var stateStore = new MemStateStore(stateDB);
    var eventStore = new MemEventStore(eventDB);
    var pipeline = pipeline(eventStore, stateStore);

    var commands = Flux.range(0, count).map(this::createUserCmd);
    var expectedEvents = Flux.range(0, count).map(this::userCreatedEvent).collectList().block();

    // When
    StepVerifier.create(commands.concatMap(pipeline))
                .expectNext(new Account("name_0"))
                .expectNext(new Account("name_1"))
                .expectNext(new Account("name_2"))
                .verifyComplete();
    // And
    Assertions.assertEquals(expectedEvents, eventDB);
  }

  private Command createUserCmd(Integer i) {
    return new CreateUser(Id.of("user_" + i), "name_" + i);
  }

  private static Pipeline pipeline(EventStore eventStore, StateStore stateStore) {
    return new Pipeline(new Visitor(), stateStore, eventStore, new UserDecider(), new UserEvolver());
  }

  private Event userCreatedEvent(Integer i) {
    return new UserCreated(Id.of("user_" + i), "name_" + i);
  }
}