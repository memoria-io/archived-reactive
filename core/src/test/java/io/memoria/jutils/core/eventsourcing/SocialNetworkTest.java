package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.User;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class SocialNetworkTest {
  private final SocialNetworkTestData testData;
  private final CommandHandler<User, UserCommand> handler;

  public SocialNetworkTest() {
    this.testData = new SocialNetworkTestData();
    this.handler = testData.statefulHandler;
  }

  @Test
  void applyAllCommands() {
    // When
    var eventFlux = handler.apply(testData.topic, Flux.just(testData.create, testData.add, testData.send));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectComplete()
                .verify();
  }

  @Test
  void applyOneCommand() {
    // When
    var eventFlux = handler.apply(testData.topic, testData.create);
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectComplete().verify();
  }

  @Test
  void applyTwice() {
    // When
    var eventFlux = handler.apply(testData.topic, Flux.just(testData.create, testData.create));
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectError(ESInvalidOperation.class).verify();
  }
}
