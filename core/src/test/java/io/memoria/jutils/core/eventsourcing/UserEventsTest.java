package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserEventsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;

  @Test
  public void friendAddedTest() {
    var alex = new User(ALEX, ALEX_AGE);
    var actualAlex = new FriendAdded(ALEX, BOB).apply(alex);
    var expectedAlex = alex.withNewFriend(BOB);
    Assertions.assertEquals(expectedAlex, actualAlex);
  }

  @Test
  public void messageCreatedTest() {
    var alex = new User(ALEX, ALEX_AGE, HashSet.of(BOB), HashSet.empty());
    var actualAlex = new MessageCreated("messageId", ALEX, BOB, "Hello").apply(alex);
    var expectedAlex = alex.withNewMessage(new Message("messageId", ALEX, BOB, "Hello"));
    Assertions.assertEquals(expectedAlex, actualAlex);
  }

  @Test
  public void multipleEventsTest() {
    var alex = new User(ALEX, ALEX_AGE);
    var events = List.of(new FriendAdded(ALEX, BOB), new MessageCreated("messageId", ALEX, BOB, "Hello"));
    var actualAlex = events.foldLeft(alex, (user, ev) -> ev.apply(user));
    var expectedAlex = alex.withNewFriend(BOB).withNewMessage(new Message("messageId", ALEX, BOB, "Hello"));
    Assertions.assertEquals(expectedAlex, actualAlex);
  }
}
