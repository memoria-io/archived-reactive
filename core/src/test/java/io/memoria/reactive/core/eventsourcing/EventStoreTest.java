package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.db.Read;
import io.memoria.reactive.core.db.Write;
import io.memoria.reactive.core.db.mem.MemRDB;
import io.memoria.reactive.core.eventsourcing.user.User.Account;
import io.memoria.reactive.core.eventsourcing.user.User.Visitor;
import io.memoria.reactive.core.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.user.UserDecider;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.user.UserEvolver;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.IdGenerator;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

class EventStoreTest {
  private static final IdGenerator idGenerator = () -> Id.of(1);

  @Test
  void handleCommands() {
    // Given
    var eventStreamDB = new MemRDB<Event>(new ArrayList<>());
    var eventStore = createEventStore(eventStreamDB, eventStreamDB);
    var count = 3;
    var commands = List.range(0, count).<Command>map(this::createCommand);
    var cmdStream = new MemRDB<>(commands.asJava());
    var expectedEvents = List.range(0, count).map(this::createEvent);

    // When
    StepVerifier.create(Flux.fromIterable(commands).concatMap(eventStore)).expectNextCount(count).verifyComplete();
    // Then
    StepVerifier.create(eventStreamDB.read(0)).expectNext(expectedEvents).verifyComplete();
    // When
    var states = ES.pipeline(cmdStream, 0, eventStore);
    // Then
    StepVerifier.create(states)
                .expectNext(new Account("name_0"))
                .expectNext(new Account("name_1"))
                .expectNext(new Account("name_2"))
                .verifyComplete();

  }

  private CreateUser createCommand(Integer i) {
    return new CreateUser(i, Id.of("user_" + i), "name_" + i);
  }

  private Event createEvent(Integer i) {
    return new UserCreated(i, Id.of("user_" + i), "name_" + i);
  }

  private static EventStore createEventStore(Read<Event> read, Write<Event> write) {
    var state = ES.buildState(read, new UserEvolver()).block();
    return new EventStore(new Visitor(), state, write, new UserDecider(new AtomicLong(0)), new UserEvolver());
  }
}