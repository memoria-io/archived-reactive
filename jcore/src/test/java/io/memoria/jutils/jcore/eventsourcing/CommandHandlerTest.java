//package io.memoria.jutils.jcore.eventsourcing;
//
//import io.memoria.jutils.jcore.eventsourcing.User.Visitor;
//import io.memoria.jutils.jcore.eventsourcing.UserCommand.CreateUser;
//import io.memoria.jutils.jcore.id.IdGenerator;
//import io.memoria.jutils.jcore.id.SerialIdGenerator;
//import io.memoria.jutils.jcore.msgbus.MsgBusAdmin;
//import io.vavr.collection.List;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.function.Supplier;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class CommandHandlerTest {
//  private static final Random r = new Random();
//  private static final IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
//  private static final Supplier<LocalDateTime> timeSupplier = () -> LocalDateTime.of(2020, 10, 10, 10, 10);
//  private static final Decider<User, UserCommand> decider = new UserDecider(idGen, timeSupplier);
//  private static final Evolver<User> evolver = new UserEvolver();
//
//  private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> eventDB;
//  private static final MsgBusAdmin eventStore;
//
//  static {
//    eventDB = new ConcurrentHashMap<>();
//    eventStore = new InMemoryEventStore(eventDB);
//  }
//
//  private final String topic;
//
//  CommandHandlerTest() {
//    topic = "topic-" + r.nextInt(100);
//    eventDB.put(topic, new ConcurrentHashMap<>());
//    eventDB.get(topic).put(0, List.empty());
//  }
//
//  @Test
//  void initialEvents() {
//    var lastEventMono = eventStore.lastEvent(topic, 0);
//    var initEvents = lastEventMono.flatMapMany(le -> eventStore.subscribe(topic, 0, 0).takeUntil(le::equals));
//    var stateStore = CommandHandler.buildState(initEvents, evolver).block();
//    var commandHandler = new CommandHandler<>(stateStore, decider, eventStore, topic, 0, evolver, new Visitor(), ser);
//    commandHandler.apply(new CreateUser(0, topic)).subscribe();
//    var userCreated = eventDB.get(topic).get(0).head();
//    assertTrue(userCreated instanceof UserCreated);
//  }
//}
