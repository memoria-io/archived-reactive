package io.memoria.jutils.core.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SocialNetworkSuite {
  public static void applyAllCommands(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(testData.topic, Flux.just(testData.create, testData.add, testData.send));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectComplete()
                .verify();
  }

  public static void applyOneCommand(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(testData.topic, testData.create);
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectComplete().verify();
  }

  public static void applyTwice(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(testData.topic, Flux.just(testData.create, testData.create));
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectError(ESInvalidOperation.class).verify();
  }
}
