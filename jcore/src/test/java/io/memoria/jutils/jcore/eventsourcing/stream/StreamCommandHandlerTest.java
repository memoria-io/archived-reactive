package io.memoria.jutils.jcore.eventsourcing.stream;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.user.User;
import io.memoria.jutils.jcore.eventsourcing.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand;
import io.memoria.jutils.jcore.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.eventsourcing.user.UserDecider;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvolver;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jcore.stream.StreamRepo;
import io.memoria.jutils.jcore.stream.mem.MemStream;
import io.memoria.jutils.jcore.text.SerializableTransformer;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class StreamCommandHandlerTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final IdGenerator idGenerator = () -> Id.of(1);
  private final CommandHandler<User, UserCommand> cmdHandler;
  private final StreamRepo streamRepo;

  StreamCommandHandlerTest() {
    // Setup
    String TOPIC = "Topic_" + new Random().nextInt(1000);
    int PARTITION = 0;

    streamRepo = new MemStream(TOPIC, PARTITION, new ConcurrentHashMap<>());
    cmdHandler = new StreamCommandHandler<>(new Visitor(),
                                            streamRepo,
                                            new UserDecider(idGenerator),
                                            new UserEvolver(),
                                            transformer);
  }

  @Test
  void handleCommands() {
    // Given
    var commands = Flux.range(0, 100).map(i -> new CreateUser(idGenerator.get(), Id.of("user_" + i), "name_" + i));
    var expectedEvents = List.range(0, 100).map(StreamCommandHandlerTest::createEventMessage);
    // When
    StepVerifier.create(commands.concatMap(cmdHandler)).expectNextCount(100).verifyComplete();
    // Then
    StepVerifier.create(streamRepo.subscribe(0)).expectNext(expectedEvents.toJavaArray(String[]::new)).verifyComplete();
  }

  private static String createEventMessage(Integer i) {
    return transformer.serialize(new UserCreated(idGenerator.get(), Id.of("user_" + i), "name_" + i)).get();
  }

}
