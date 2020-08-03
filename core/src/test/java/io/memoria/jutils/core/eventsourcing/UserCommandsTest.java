package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  @Test
  public void addFriendTest() {
    var user = new User(ALEX, ALEX_AGE);
    var events = new AddFriend(ALEX, BOB).apply(user);
    Assertions.assertThat(events).isEqualTo(Try.success(List.of(new FriendAdded(ALEX, BOB))));

    var otherUser = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var otherEvents = new AddFriend(ALEX, BOB).apply(otherUser);
    Assertions.assertThat(otherEvents).isEqualTo(Try.failure(ALREADY_EXISTS));
  }

  @Test
  public void sendMessage() {
    var user = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var expectedEvent = new MessageCreated("messageId", ALEX, BOB, "hello");
    var events = new SendMessage(ALEX, BOB, "messageId", "hello").apply(user);
    Assertions.assertThat(events).isEqualTo(Try.success(List.of(expectedEvent)));
  }
}
