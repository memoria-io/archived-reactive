package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.usecase.user.User;
import io.memoria.jutils.jcore.usecase.user.User.Visitor;
import io.memoria.jutils.jcore.usecase.user.UserCommand;
import io.memoria.jutils.jcore.usecase.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.usecase.user.UserDecider;
import io.memoria.jutils.jcore.usecase.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.usecase.user.UserEvolver;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class CommandHandlerTest {
  private final CommandHandler<User, UserCommand> cmdHandler;
  private final EventStream eventStream;

  CommandHandlerTest() {
    // Setup
    String TOPIC = "Topic_" + new Random().nextInt(1000);
    int PARTITION = 0;
    eventStream = new MemEventStream(TOPIC, PARTITION, new ConcurrentHashMap<>());
    cmdHandler = new CommandHandler<>(new Visitor(),
                                      new ConcurrentHashMap<>(),
                                      eventStream,
                                      new UserDecider(() -> Id.of(1)),
                                      new UserEvolver());
  }

  @Test
  void handleCommands() {
    // Given
    var commands = Flux.range(0, 100).map(i -> new CreateUser(Id.of(i), Id.of("bob_id" + i), "bob_name" + i));
    var expectedEvents = List.range(0, 100)
                             .map(i -> (Event) new UserCreated(Id.of(1), Id.of("bob_id" + i), "bob_name" + i));
    // When
    StepVerifier.create(commands.concatMap(cmdHandler)).expectNextCount(100).verifyComplete();
    // Then
    StepVerifier.create(eventStream.subscribe(0)).expectNext(expectedEvents.toJavaArray(Event[]::new)).verifyComplete();
  }
}
