package io.memoria.jutils.core.eventsourcing.test;

import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.core.Err.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;
  private static final UserCommandHandler commandHandler = new UserCommandHandler();

  @Test
  public void addFriendTest() {
    var user = new User(ALEX, ALEX_AGE);
    var events = commandHandler.apply(user, new AddFriend(ALEX, BOB));
    Assertions.assertEquals(Try.success(List.of(new FriendAdded(ALEX, BOB))), events);

    var otherUser = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var otherEvents = commandHandler.apply(otherUser, new AddFriend(ALEX, BOB));
    Assertions.assertEquals(Try.failure(ALREADY_EXISTS), otherEvents);
  }

  @Test
  public void sendMessageTest() {
    var user = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var events = commandHandler.apply(user, new SendMessage(ALEX, BOB, "hello"));
    Assertions.assertEquals(Try.success(List.of(new MessageCreated("messageId", ALEX, BOB, "hello"))), events);
  }
}
