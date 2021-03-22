package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.data.user.User;
import io.memoria.jutils.jcore.eventsourcing.data.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserDecider;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvolver;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandHandlerTest {
  private final String TOPIC = "Topic_" + new Random().nextInt(1000);
  private final int PARTITION = 0;

  private final ConcurrentHashMap<Id, User> stateStore;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> eventStoreDB;
  private final CommandHandler<User, UserCommand> cmdHandler;

  CommandHandlerTest() {
    // Constants
    IdGenerator idGen = () -> Id.of(1);
    var decider = new UserDecider(idGen);
    // Setup
    eventStoreDB = new ConcurrentHashMap<>();
    var eventStore = new MemEventStore(TOPIC, PARTITION, eventStoreDB);
    var evolver = new UserEvolver();
    stateStore = CommandHandler.createStateStore(eventStore, evolver).block();
    cmdHandler = new CommandHandler<>(new Visitor(), stateStore, eventStore, decider, evolver);
  }

  @Test
  void handleCommands() {
    // When
    Flux.range(0, 100)
        .concatMap(i -> cmdHandler.apply(new CreateUser(Id.of(i), Id.of("bob_id" + i), "bob_name" + i)))
        .blockLast();
    // Then
    List.range(0, 100)
        .forEach(i -> assertEquals(new User.Account("bob_name" + i), stateStore.get(Id.of("bob_id" + i))));
  }
}
