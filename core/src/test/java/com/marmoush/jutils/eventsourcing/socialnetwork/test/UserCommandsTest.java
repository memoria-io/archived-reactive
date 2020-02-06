package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserCommand.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.FriendAdded;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.Inbox;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;

import static com.marmoush.jutils.core.domain.error.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  @Test
  public void addFriendTest() {
    var user = new User(ALEX, ALEX_AGE);
    var events = new AddFriend(ALEX, BOB).apply(user);
    Assertions.assertEquals(Try.success(List.of(new FriendAdded(ALEX, BOB))), events);

    var otherUser = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var otherEvents = new AddFriend(ALEX, BOB).apply(otherUser);
    Assertions.assertEquals(Try.failure(ALREADY_EXISTS), otherEvents);
  }

  @Test
  void sendMessageTest() {
    var user = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var events = new SendMessage(ALEX, BOB, "hello").apply(user);
    Assertions.assertEquals(Try.success(List.of(new UserEvent.MessageCreated(ALEX, BOB, "hello"))), events);
  }
}
