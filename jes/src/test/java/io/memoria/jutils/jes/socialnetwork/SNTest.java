package io.memoria.jutils.jes.socialnetwork;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.ESException.InvalidOperation;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jes.socialnetwork.domain.UserCommand;
import io.memoria.jutils.jes.socialnetwork.domain.UserCommand.SendMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.LastStep;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

class SNTest {
  private static final AtomicInteger atomic = new AtomicInteger();
  private static final IdGenerator idGenerator = () -> Id.of(atomic.getAndIncrement() + "");
  private final SNTestData testData = new SNTestData(new Random(), idGenerator);

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void failurePath(CommandHandler<UserCommand> handler) {
    // When
    var create = handler.apply(testData.create);
    // Then
    StepVerifier.create(create).expectComplete().verify();
    StepVerifier.create(create).expectError(InvalidOperation.class).verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void happyPath(CommandHandler<UserCommand> handler) {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var result = commands.map(handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    // Then
    StepVerifier.create(result).expectNextCount(3).expectComplete().verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void manyCommands(CommandHandler<UserCommand> handler) {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var init = commands.map(handler).map(StepVerifier::create).map(LastStep::expectComplete).map(StepVerifier::verify);
    // Then
    StepVerifier.create(init).expectNextCount(3).expectComplete().verify();

    // Given
    var sendFlux = Flux.range(0, 100)
                       .map(i -> new SendMessage(Id.of("cmd_" + i), testData.userId, testData.friendId, "hello_" + i));
    // When
    var result = sendFlux.map(handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    StepVerifier.create(result).expectNextCount(100).expectComplete().verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void oneCommand(CommandHandler<UserCommand> handler) {
    // When
    var eventFlux = handler.apply(testData.create);
    // Then
    StepVerifier.create(eventFlux).expectComplete().verify();
  }

  private static Stream<Arguments> commandHandlers() {
    return Stream.of(of(SNTestUtils.r2CH(idGenerator)));
  }
}
