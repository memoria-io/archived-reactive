package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.EventHandler;
import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.CommandHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.AddFriend;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.SendMessage;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Inbox;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Message;
import com.marmoush.jutils.general.adapter.generator.id.SerialIdGenerator;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class CmdTest {
  private EventHandler<User, UserEvent> userEventHandler = new UserEventHandler(new SerialIdGenerator(new AtomicInteger()));
  private CommandHandler<User, UserCommand, Event> commandHandler = new UserCommandHandler();

  @Test
  public void writerTest() {
    var user = new User("bob", 19); // TODO decide entity or value
    var add = new AddFriend("2", "user1", "user2"); // TODO handle ordering
    var send = new SendMessage("1", "user1", "user2", "hello");
    var commands = Flux.just(add, send);
    var mono = applyCommands(user, commands);
    var expectedMessage = new Message("user1", "user2", "hello", false);
    var expectedInbox = new Inbox().withNewMessage(expectedMessage);
    var expectedUser = new User("bob", 19, List.of("user2"), expectedInbox);
    StepVerifier.create(mono).expectNextMatches(p -> p.get()._1.equals(expectedUser)).expectComplete().verify();
  }

  private Mono<Try<Tuple2<User, List<Event>>>> applyCommands(User user, Flux<UserCommand> commands) {
    return commands.reduce(Try.success(Tuple.of(user, List.empty())), this::applyCommand);
  }

  private Try<Tuple2<User, List<Event>>> applyCommand(Try<Tuple2<User, List<Event>>> t, UserCommand cmd) {
    return t.flatMap(tpl -> {
      var tryEvents = commandHandler.apply(tpl._1, cmd);
      var tryUser = tryEvents.map(events -> evolve(tpl._1, events));
      var accumulateEvents = tryEvents.map(e -> e.appendAll(tpl._2));
      return tryUser.flatMap(user -> accumulateEvents.map(events -> Tuple.of(user, events)));
    });
  }

  private User evolve(User user, List<Event> e) {
    return e.foldLeft(user,
                      (u, event) -> Match(event).of(Case($(instanceOf(UserEvent.class)), userEventHandler.apply(u))));
  }
}
