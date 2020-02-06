package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.User;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.*;

public class UserEventsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  @Test
  void messageCreatedTest() {
    var alex = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var newAlex = new MessageCreated(ALEX, BOB, "Hello").apply(alex);
    var expectedAlex = alex.withNewMessage(new Message(ALEX, BOB, "Hello", false));
    Assertions.assertEquals(expectedAlex, newAlex);
  }

  @Test
  void friendAddedTest() {
    var alex = new User(ALEX, ALEX_AGE);
    var newAlex = new FriendAdded(ALEX, BOB).apply(alex);
    var expectedAlex = alex.withNewFriend(BOB);
    Assertions.assertEquals(expectedAlex, newAlex);
  }

  @Test
  void multipleEvents() {
    var alex = new User(ALEX, ALEX_AGE);
    var list = List.of(new FriendAdded(ALEX, BOB), new MessageCreated(ALEX, BOB, "Hello"));
    var newAlex = list.foldLeft(alex, (u, e) -> e.apply(u));
    var expectedAlex = alex.withNewFriend(BOB).withNewMessage(new Message(ALEX, BOB, "Hello"));
    Assertions.assertEquals(expectedAlex, newAlex);
  }
}
