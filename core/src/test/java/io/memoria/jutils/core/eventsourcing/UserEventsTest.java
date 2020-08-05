package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.OnlineUser;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageCreated;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.OnlineUserCreated;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEventHandler;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class UserEventsTest {
  private static final UserEventHandler eventHandler = new UserEventHandler();
  // Scenarios Data
  private static final String ALEX_NAME = "alex";
  private static final String BOB_NAME = "bob";
  private static final int ALEX_AGE = 19;
  private static final OnlineUser ALEX = new OnlineUser(ALEX_NAME, ALEX_AGE);
  private static final OnlineUserCreated ONLINE_USER_CREATED = new OnlineUserCreated(ALEX_NAME, ALEX_AGE);
  private static final MessageCreated MESSAGE_CREATED = new MessageCreated("messageId", ALEX_NAME, BOB_NAME, "Hello");
  private static final FriendAdded FRIEND_ADDED = new FriendAdded(ALEX_NAME, BOB_NAME);
  private static final Message MESSAGE = new Message("messageId", ALEX_NAME, BOB_NAME, "Hello");

  @Test
  public void eventsFlux() {
    // Given
    Flux<UserEvent> events = Flux.just(FRIEND_ADDED, MESSAGE_CREATED);
    // When
    var newAlexState = events.reduce(ALEX, eventHandler);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE);
    StepVerifier.create(newAlexState).expectNext(expectedAlex).expectComplete().verify();
  }

  @Test
  public void eventsList() {
    // Given
    var events = List.of(ONLINE_USER_CREATED, FRIEND_ADDED, MESSAGE_CREATED);
    // When
    var newAlexState = events.foldLeft(new User() {}, eventHandler);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE);
    assertThat(newAlexState).isEqualTo(expectedAlex);
  }

  @Test
  public void friendAddedTest() {
    // When
    var alex = new FriendAdded(ALEX_NAME, BOB_NAME).apply(ALEX);
    // Then
    assertThat(alex).isEqualTo(ALEX.withNewFriend(BOB_NAME));
  }

  @Test
  public void messageCreatedTest() {
    // Given
    var alex = ALEX.withNewFriend(BOB_NAME);
    // When
    var newAlexState = MESSAGE_CREATED.apply(alex);
    // Then
    var expectedAlex = alex.withNewMessage(new Message("messageId", ALEX_NAME, BOB_NAME, "Hello"));
    assertThat(newAlexState).isEqualTo(expectedAlex);
  }
}
