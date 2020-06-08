package io.memoria.jutils.eventsourcing.socialnetwork.test;

import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.Message;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.User;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.FriendAdded;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.MessageCreated;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.MessageSeen;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.UserEventHandler;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserEventsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  private static UserEventHandler userEventHandler = new UserEventHandler();

  @Test
  public void messageCreatedTest() {
    var alex = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var actualAlex = userEventHandler.apply(alex, new MessageCreated("messageId", ALEX, BOB, "Hello"));
    var expectedAlex = alex.withNewMessage(new Message("messageId", ALEX, BOB, "Hello"));
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
                                                    new MessageCreated("messageId", ALEX, BOB, "Hello"),
                                                    new MessageSeen(BOB, "messageId")));
    var expectedAlex = alex.withNewFriend(BOB)
                           .withNewMessage(new Message("messageId", ALEX, BOB, "Hello"))
                           .withMessageSeen("messageId", true);
    Assertions.assertEquals(expectedAlex, actualAlex);
  }
}
