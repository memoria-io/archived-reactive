package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.domain.port.EventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.*;

public class UserEventsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  private static EventHandler<User, UserEvent> userEventHandler = new UserEventHandler();

  @Test
  void messageCreatedTest() {
    var alex = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var newAlex = userEventHandler.apply(alex, new MessageCreated(ALEX, BOB, "Hello"));
    var expectedAlex = alex.withNewMessage(new Message(ALEX, BOB, "Hello", false));
    Assertions.assertEquals(expectedAlex, newAlex);
  }

  @Test
  void friendAddedTest() {
    var alex = new User(ALEX, ALEX_AGE);
    var newAlex = userEventHandler.apply(alex, new FriendAdded(ALEX, BOB));
    var expectedAlex = alex.withNewFriend(BOB);
    Assertions.assertEquals(expectedAlex, newAlex);
  }

  @Test
  void multipleEvents() {
    var alex = new User(ALEX, ALEX_AGE);
    var newAlex = userEventHandler.curried(alex)
                                  .apply(List.of(new FriendAdded(ALEX, BOB), new MessageCreated(ALEX, BOB, "Hello")));
    var expectedAlex = alex.withNewFriend(BOB).withNewMessage(new Message(ALEX, BOB, "Hello"));
    Assertions.assertEquals(expectedAlex, newAlex);
  }
}
