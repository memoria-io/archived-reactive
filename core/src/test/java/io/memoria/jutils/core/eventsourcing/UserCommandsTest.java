package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.OnlineUser;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageCreated;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  private static final String ALEX_NAME = "alex";
  private static final String BOB_NAME = "bob";
  private static final int ALEX_AGE = 19;
  private static final OnlineUser ALEX = new OnlineUser(ALEX_NAME, ALEX_AGE);
  private static final MessageCreated MESSAGE_CREATED = new MessageCreated("messageId", ALEX_NAME, BOB_NAME, "Hello");
  private static final AddFriend ADD_FRIEND = new AddFriend(ALEX_NAME, BOB_NAME);

  @Test
  public void addFriendTest() {
    var events = ADD_FRIEND.apply(ALEX);
    StepVerifier.create(events).expectNext(new FriendAdded(ALEX_NAME, BOB_NAME));

    var otherUser = new OnlineUser(ALEX_NAME, ALEX_AGE, HashSet.of(BOB_NAME), HashSet.empty());
    var otherEvents = ADD_FRIEND.apply(otherUser);
    StepVerifier.create(otherEvents).expectError(ALREADY_EXISTS.getClass());
  }

  @Test
  public void sendMessage() {
    var user = new OnlineUser(ALEX_NAME, ALEX_AGE, HashSet.of(BOB_NAME), HashSet.empty());
    var events = new SendMessage(ALEX_NAME, BOB_NAME, "hello").apply(user, "messageId");
    StepVerifier.create(events).expectNext(MESSAGE_CREATED);
  }
}
