package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.tests;

import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class UserEvolverTest {
  private static final UserEvolver evolver = new UserEvolver();
  // Scenarios Data
  private static final String ALEX_NAME = "alex";
  private static final String BOB_NAME = "bob";
  private static final int ALEX_AGE = 19;
  private static final Account ALEX = new Account(ALEX_NAME, ALEX_AGE);
  private static final Message MESSAGE = new Message("messageId", ALEX_NAME, BOB_NAME, "Hello");
  private static final MessageSent MESSAGE_SENT = new MessageSent("0", MESSAGE);
  private static final FriendAdded FRIEND_ADDED = new FriendAdded("0", BOB_NAME);

  @Test
  public void eventsFlux() {
    // Given
    Flux<UserEvent> events = Flux.just(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.reduce(ALEX, evolver);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE.id());
    StepVerifier.create(newAlexState).expectNext(expectedAlex).expectComplete().verify();
  }

  @Test
  public void eventsList() {
    // Given
    var events = List.of(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.foldLeft(ALEX, evolver);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE.id());
    assertThat(newAlexState).isEqualTo(expectedAlex);
  }

  @Test
  public void friendAddedTest() {
    // When
    var user = evolver.apply(ALEX, FRIEND_ADDED);
    // Then
    assertThat(user).isEqualTo(ALEX.withNewFriend(BOB_NAME));
  }

  @Test
  public void messageCreatedTest() {
    // Given
    var alex = ALEX.withNewFriend(BOB_NAME);
    // When
    var user = evolver.apply(alex, MESSAGE_SENT);
    // Then
    var expectedAlex = alex.withNewMessage(MESSAGE.id());
    assertThat(user).isEqualTo(expectedAlex);
  }
}
