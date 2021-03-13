package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.UserCommand.CreateUser;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jcore.id.SerialIdGenerator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

class CommandHandlerTest {
  private static final Random r = new Random();
  private static final IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
  private static final Supplier<LocalDateTime> timeSupplier = () -> LocalDateTime.of(2020, 10, 10, 10, 10);
  private static final Decider<User, UserCommand> decider = new UserDecider(idGen, timeSupplier);
  private static final Evolver<User> evolver = new UserEvolver();

  private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> eventDB;
  private static final EventStore eventStore;

  static {
    eventDB = new ConcurrentHashMap<>();
    eventStore = new InMemoryEventStore(eventDB);
  }

  private final String topic;

  CommandHandlerTest() {
    topic = "topic-" + r.nextInt(100);
    System.out.println(topic);
    eventDB.put(topic, new ConcurrentHashMap<>());
    eventDB.get(topic).put(0, Flux.empty());
  }

  @Test
  void initialEvents() {
    var initEvents = eventStore.readAll(topic, 0);
    var stateStore = CommandHandler.buildState(initEvents, evolver).block();
    var commandHandler = new CommandHandler<>(stateStore, decider, eventStore, topic, 0, evolver, new Visitor());
    commandHandler.handle(new CreateUser(0, topic)).subscribe();
    eventDB.get(topic).get(0).subscribe(System.out::println);
  }
}
