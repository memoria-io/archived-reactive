package io.memoria.jutils.r2es.socialnetwork;

import io.memoria.jutils.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.sql.SQLException;

class SocialNetworkTest {
  private final SocialNetworkTestData testData;

  SocialNetworkTest() throws SQLException {
    this.testData = new SocialNetworkTestData(false);
  }

  @Test
  void failurePath() {
    // When
    var eventFlux = testData.handler.apply(Flux.just(testData.create, testData.create));
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectError(InvalidOperation.class).verify();
  }

  @Test
  void happyPath() {
    // When
    var eventFlux = testData.handler.apply(Flux.just(testData.create, testData.add, testData.send));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectComplete()
                .verify();
  }

  @Test
  void manyCommands() {
    // Given
    var createAddSend = Flux.just(testData.create, testData.add, testData.send);
    var sendFlux = Flux.range(0, 100)
                       .map(i -> new SendMessage(Id.of("cmd_" + i), testData.userId, testData.friendId, "hello_" + i));
    // When
    var eventFlux = testData.handler.apply(createAddSend.concatWith(sendFlux));
    // Then
    StepVerifier.create(eventFlux)
                .expectNext(testData.accountCreated, testData.friendAdded, testData.messageSent)
                .expectNextCount(100)
                .expectComplete()
                .verify();
  }

  @Test
  void oneCommand() {
    // When
    var eventFlux = testData.handler.apply(testData.create);
    // Then
    StepVerifier.create(eventFlux).expectNext(testData.accountCreated).expectComplete().verify();
  }
}
