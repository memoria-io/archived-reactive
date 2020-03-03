package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.User;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.*;

public class UserEventsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  private static UserEventHandler userEventHandler = new UserEventHandler();

  @Test
  public void messageCreatedTest() {
    var alex = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var actualAlex = userEventHandler.apply(alex, new MessageCreated("messageId", ALEX, BOB, "Hello"));
    var expectedAlex = alex.withNewMessage(new Message("messageId", ALEX, BOB, "Hello", false));
    Assertions.assertEquals(expectedAlex, actualAlex);
  }

  @Test
  public void friendAddedTest() {
    var alex = new User(ALEX, ALEX_AGE);
    var actualAlex = userEventHandler.apply(alex, new FriendAdded(ALEX, BOB));
    var expectedAlex = alex.withNewFriend(BOB);
    Assertions.assertEquals(expectedAlex, actualAlex);
  }

  @Test
  public void multipleEvents() {
    var alex = new User(ALEX, ALEX_AGE);
    var actualAlex = userEventHandler.apply(alex,
                                            List.of(new FriendAdded(ALEX, BOB),
                                                    new MessageCreated("messageId", ALEX, BOB, "Hello")));
    var expectedAlex = alex.withNewFriend(BOB).withNewMessage(new Message("messageId", ALEX, BOB, "Hello"));
    Assertions.assertEquals(expectedAlex, actualAlex);
  }
}
