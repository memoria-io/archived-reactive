package io.memoria.jutils.core.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SocialNetworkSuite {
  public static void failurePath(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(Flux.just(testData.create, testData.create));
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectError(InvalidOperation.class).verify();
  }

  public static void happyPath(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(Flux.just(testData.create, testData.add, testData.send));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectComplete()
                .verify();
  }

  public static void manyCommands(SocialNetworkTestData testData) {
    // Given
    var createAddSend = Flux.just(testData.create, testData.add, testData.send);
    var sendFlux = Flux.range(0, 100)
                       .map(i -> new SendMessage(new Id("cmd_" + i), testData.userId, testData.friendId, "hello_" + i));
    // When
    var eventFlux = testData.handler.apply(createAddSend.concatWith(sendFlux));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectNextCount(100)
                .expectComplete()
                .verify();
  }

  public static void oneCommand(SocialNetworkTestData testData) {
    // When
    var eventFlux = testData.handler.apply(testData.create);
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectComplete().verify();
  }
}
