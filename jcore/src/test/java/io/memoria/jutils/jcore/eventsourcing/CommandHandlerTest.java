package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.data.user.User;
import io.memoria.jutils.jcore.eventsourcing.data.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserDecider;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvolver;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserTextTransformer;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.SerialIdGenerator;
import io.memoria.jutils.jcore.msgbus.MemPublisher;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class CommandHandlerTest {
  private final ConcurrentHashMap<Id, User> stateStore;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> eventStore;
  private final CommandHandler<User, UserCommand> cmdHandler;

  CommandHandlerTest() {
    stateStore = new ConcurrentHashMap<>();
    eventStore = new ConcurrentHashMap<>();
    var topic = "Topic_" + new Random().nextInt(1000);
    var idGen = new SerialIdGenerator(new AtomicLong());
    var publisher = new MemPublisher(topic, 0, eventStore);
    var transformer = new UserTextTransformer();
    var decider = new UserDecider(idGen, () -> LocalDateTime.of(2020, 10, 10, 10, 10));
    var evolver = new UserEvolver();
    cmdHandler = new CommandHandler<>(new Visitor(), stateStore, decider, evolver, transformer, publisher);
  }

  @Test
  void initialEvents() {
    //    var lastEventMono = eventStore.lastEvent(topic, 0);
    //    var initEvents = lastEventMono.flatMapMany(le -> eventStore.subscribe(topic, 0, 0).takeUntil(le::equals));
    //    var stateStore = CommandHandler.buildState(initEvents, evolver).block();
    //    var commandHandler = new CommandHandler<>(stateStore, decider, eventStore, topic, 0, evolver, new Visitor(), ser);
    //    commandHandler.apply(new CreateUser(0, topic)).subscribe();
    //    var userCreated = eventDB.get(topic).get(0).head();
    //    assertTrue(userCreated instanceof UserEvent.UserCreated);
  }
}
