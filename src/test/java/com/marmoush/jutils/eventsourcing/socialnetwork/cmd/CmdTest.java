package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.msg.MessageEventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.*;
import com.marmoush.jutils.general.adapter.generator.id.SerialIdGenerator;
import com.marmoush.jutils.utils.functional.VavrUtils;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class CmdTest {
  private UserEventHandler userEventHandler = new UserEventHandler();
  private MessageEventHandler messageEventHandler=new MessageEventHandler();
  @Test
  public void writerTest() {
    var commandHandler = new UserCommandHandler(new SerialIdGenerator(new AtomicInteger()));
    var user1 = new UserEntity("user1", new User("name1", 19, List.empty()));
    var add = new AddFriend("2", "user1", "user2"); // TODO handle ordering
    var send = new SendMessage("1", "user1", "user2", "hello");
    var commands = Flux.just(add, send);
    var monoUser = commands.map(commandHandler.apply(user1.value))
                           .map(VavrUtils::listOfTry)
                           .flatMap(Flux::fromIterable)
                           .flatMap(Flux::fromIterable)// TODO handle failure
                           .reduce(user1.value, (u, e) -> match(e, u));

  }

  private User match(Event e, User user) {
    return Match(e).of(Case($(instanceOf(UserEvent.class)), userEventHandler.apply(user)));
  }
}
