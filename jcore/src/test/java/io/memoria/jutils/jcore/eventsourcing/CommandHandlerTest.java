//package io.memoria.jutils.jcore.eventsourcing;
//
//import io.memoria.jutils.jcore.eventsourcing.data.user.User;
//import io.memoria.jutils.jcore.eventsourcing.data.user.User.Account;
//import io.memoria.jutils.jcore.eventsourcing.data.user.User.Visitor;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand.CreateUser;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserDecider;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvent.UserCreated;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvolver;
//import io.memoria.jutils.jcore.eventsourcing.data.user.UserTextTransformer;
//import io.memoria.jutils.jcore.id.Id;
//import io.memoria.jutils.jcore.id.IdGenerator;
//import io.memoria.jutils.jcore.msgbus.MemPublisher;
//import io.memoria.jutils.jcore.msgbus.MemSubscriber;
//import io.memoria.jutils.jcore.text.TextTransformer;
//import io.vavr.collection.List;
//import io.vavr.control.Try;
//import org.junit.jupiter.api.Test;
//import reactor.core.publisher.Flux;
//
//import java.time.LocalDateTime;
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class CommandHandlerTest {
//  private final String topic = "Topic_" + new Random().nextInt(1000);
//  private final int partition = 0;
//  private final TextTransformer transformer = new UserTextTransformer();
//
//  private final ConcurrentHashMap<Id, User> stateStore;
//  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> eventStore;
//  private final CommandHandler<User, UserCommand> cmdHandler;
//
//  CommandHandlerTest() {
//    // Constants
//    IdGenerator idGen = () -> Id.of(1);
//    var decider = new UserDecider(idGen, () -> LocalDateTime.of(2020, 10, 10, 10, 10));
//    // Setup
//    eventStore = new ConcurrentHashMap<>();
//    var msgBusAdmin = new MemEventStoreAdmin(eventStore);
//    var offset = msgBusAdmin.currentOffset(topic, partition).onErrorReturn(0L).block();
//    assert offset != null;
//    var publisher = new MemPublisher(topic, partition, eventStore);
//    var subscriber = new MemSubscriber(topic, partition, offset.intValue(), eventStore);
//    var initEvents = subscriber.subscribe()
//                               .onErrorResume(t -> Flux.empty())
//                               .map(msg -> transformer.deserialize(msg, Event.class))
//                               .map(Try::get);
//    var evolver = new UserEvolver();
//    stateStore = CommandHandler.buildState(initEvents, evolver).block();
//    cmdHandler = new CommandHandler<>(new Visitor(), stateStore, decider, evolver, transformer, publisher);
//  }
//
//  @Test
//  void initialEvents() {
//    cmdHandler.apply(new CreateUser(0, "bob")).subscribe();
//    var userCreatedMsg = eventStore.get(topic).get(partition).head();
//    var userCreated = transformer.deserialize(userCreatedMsg, Event.class).get();
//    assertTrue(userCreated instanceof UserCreated);
//    assertTrue(stateStore.get(Id.of("bob")) instanceof Account);
//  }
//}
