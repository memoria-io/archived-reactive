package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.tests;

import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.value.Id;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class UserEvolverTest {
  private static final UserEvolver evolver = new UserEvolver();
  // Scenarios Data
  private static final Id ALEX_NAME = new Id("alex");
  private static final Id BOB_NAME = new Id("bob");
  private static final int ALEX_AGE = 19;
  private static final Account ALEX = new Account(ALEX_NAME, ALEX_AGE);
  private static final Message MESSAGE = new Message(new Id("messageId"), ALEX_NAME, BOB_NAME, "Hello");
  private static final MessageSent MESSAGE_SENT = new MessageSent(new Id("0"), MESSAGE);
  private static final FriendAdded FRIEND_ADDED = new FriendAdded(new Id("0"), BOB_NAME);

  @Test
  void eventsFlux() {
    // Given
    Flux<UserEvent> events = Flux.just(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.reduce(ALEX, evolver);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE.id());
    StepVerifier.create(newAlexState).expectNext(expectedAlex).expectComplete().verify();
  }

  @Test
  void eventsList() {
    // Given
    var events = List.of(FRIEND_ADDED, MESSAGE_SENT);
    // When
    var newAlexState = events.foldLeft(ALEX, evolver);
    // Then
    var expectedAlex = ALEX.withNewFriend(BOB_NAME).withNewMessage(MESSAGE.id());
    Assertions.assertEquals(expectedAlex, newAlexState);
  }

  @Test
  void friendAddedTest() {
    // When
    var user = evolver.apply(ALEX, FRIEND_ADDED);
    // Then
    Assertions.assertEquals(ALEX.withNewFriend(BOB_NAME), user);
  }

  @Test
  void messageCreatedTest() {
    // Given
    var alex = ALEX.withNewFriend(BOB_NAME);
    // When
    var user = evolver.apply(alex, MESSAGE_SENT);
    // Then
    var expectedAlex = alex.withNewMessage(MESSAGE.id());
    Assertions.assertEquals(expectedAlex, user);
  }
}
