package io.memoria.jutils.r2es.socialnetwork;

import io.memoria.jutils.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.jutils.r2es.R2CommandHandler;
import io.memoria.jutils.r2es.socialnetwork.domain.User;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.value.Id;
import io.r2dbc.spi.ConnectionFactories;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.LastStep;

import java.sql.SQLException;

class SocialNetworkTest {
  private final SocialNetworkTestData testData;

  SocialNetworkTest() throws SQLException {
    this.testData = new SocialNetworkTestData(true);
  }

  @Test
  void failurePath() {
    // When
    var create = testData.handler.apply(testData.create);
    // Then
    StepVerifier.create(create).expectComplete().verify();
    StepVerifier.create(create).expectError(InvalidOperation.class).verify();
  }

  @Test
  void happyPath() {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var result = commands.map(testData.handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    // Then
    StepVerifier.create(result).expectNextCount(3).expectComplete().verify();
  }

  @Test
  void manyCommands() {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var init = commands.map(testData.handler)
                       .map(StepVerifier::create)
                       .map(LastStep::expectComplete)
                       .map(StepVerifier::verify);
    // Then
    StepVerifier.create(init).expectNextCount(3).expectComplete().verify();

    // Given
    var sendFlux = Flux.range(0, 100)
                       .map(i -> new SendMessage(Id.of("cmd_" + i), testData.userId, testData.friendId, "hello_" + i));
    // When
    var result = sendFlux.map(testData.handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    StepVerifier.create(result).expectNextCount(100).expectComplete().verify();
  }

  @Test
  void oneCommand() {
    // When
    var eventFlux = testData.handler.apply(testData.create);
    // Then
    StepVerifier.create(eventFlux).expectComplete().verify();
  }
}
