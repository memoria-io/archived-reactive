package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.OnlineUser;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEventHandler;
import io.vavr.collection.List;
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
  private static final MessageSent MESSAGE_SENT = new MessageSent(ALEX_NAME,
                                                                  "messageId",
                                                                  BOB_NAME,
                                                                  "Hello");
  private static final FriendAdded FRIEND_ADDED = new FriendAdded(ALEX_NAME, BOB_NAME);
  private static final Message MESSAGE = new Message("messageId", ALEX_NAME,BOB_NAME, "Hello");

  @Test
  public void eventsFlux() {
    // Given
    Flux<UserEvent> events = Flux.just(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.reduce(ALEX, eventHandler);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE);
    StepVerifier.create(newAlexState).expectNext(expectedAlex).expectComplete().verify();
  }

  @Test
  public void eventsList() {
    // Given
    var events = List.of(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.foldLeft(ALEX, eventHandler);
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
    var newAlexState = MESSAGE_SENT.apply(alex);
    // Then
    var expectedAlex = alex.withNewMessage(MESSAGE);
    assertThat(newAlexState).isEqualTo(expectedAlex);
  }
}
