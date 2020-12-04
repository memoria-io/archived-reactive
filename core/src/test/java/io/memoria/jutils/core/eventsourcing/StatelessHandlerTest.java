package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

class StatelessHandlerTest {

  @Test
  void applyAllCommands() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var handle = testData.handler.apply(testData.topic, Flux.just(testData.create, testData.add, testData.send));
    // Then
    StepVerifier.create(handle)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectComplete()
                .verify();
  }

  @Test
  void applyOneCommand() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var handle = testData.handler.apply(testData.topic, testData.create);
    // Then
    StepVerifier.create(handle).expectNext(List.of(testData.accountCreated)).expectComplete().verify();
  }

  @Test
  void applyTwice() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var handle = testData.handler.apply(testData.topic, Flux.just(testData.create, testData.create));
    // Then
    StepVerifier.create(handle).expectNext(testData.accountCreated).expectError(ESInvalidOperation.class).verify();
  }
}
