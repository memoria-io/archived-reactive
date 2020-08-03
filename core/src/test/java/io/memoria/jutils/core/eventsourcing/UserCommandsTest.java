package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.JutilsException.AlreadyExists;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class UserCommandsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;
  private static final UserCommandHandler commandHandler = new UserCommandHandler();

  @Test
  public void addFriendTest() {
    var user = new User(ALEX, ALEX_AGE);
    var events = commandHandler.apply(user, new AddFriend(ALEX, BOB));
    StepVerifier.create(events).expectNext(new FriendAdded(ALEX, BOB)).expectComplete().verify();

    var otherUser = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var otherEvents = commandHandler.apply(otherUser, new AddFriend(ALEX, BOB));
    StepVerifier.create(otherEvents).expectError(AlreadyExists.class).verify();
  }

  @Test
  public void sendMessageTest() {
    var user = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var events = commandHandler.apply(user, new SendMessage(ALEX, BOB, "hello"));
    StepVerifier.create(events)
                .expectNext(new MessageCreated("messageId", ALEX, BOB, "hello"))
                .expectComplete()
                .verify();
  }
}
