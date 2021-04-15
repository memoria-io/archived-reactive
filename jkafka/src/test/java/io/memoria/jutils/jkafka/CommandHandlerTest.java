package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStream;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.SerializableTransformer;
import io.memoria.jutils.jkafka.data.user.User;
import io.memoria.jutils.jkafka.data.user.User.Visitor;
import io.memoria.jutils.jkafka.data.user.UserCommand;
import io.memoria.jutils.jkafka.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jkafka.data.user.UserCommand.SendMessage;
import io.memoria.jutils.jkafka.data.user.UserDecider;
import io.memoria.jutils.jkafka.data.user.UserEvent.MessageSent;
import io.memoria.jutils.jkafka.data.user.UserEvent.UserCreated;
import io.memoria.jutils.jkafka.data.user.UserEvolver;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class CommandHandlerTest {

  private final CommandHandler<User, UserCommand> cmdHandler;
  private final EventStream eventStream;
  private final Id eventId = Id.of(0);
  private final Id commandId = Id.of(1);

  CommandHandlerTest() {
    String topic = "Topic_1" + new Random().nextInt(1000);
    KafkaAdmin.create("localhost:9091,localhost:9092,localhost:9093").createTopic(topic, 2, 1).block();
    this.eventStream = KafkaEventStream.create(Tests.producerConf,
                                               Tests.consumerConf,
                                               topic,
                                               0,
                                               new SerializableTransformer());
    cmdHandler = new CommandHandler<>(new Visitor(),
                                      new ConcurrentHashMap<>(),
                                      eventStream,
                                      new UserDecider(() -> eventId),
                                      new UserEvolver());
  }

  @Test
  void handleCommands() {
    // Given
    var cmds = Flux.range(0, 100).concatMap(i -> Flux.just(createUser(i), sendMessage(i)));
    var expectedEvents = List.range(0, 100).flatMap(i -> List.<Event>of(userCreated(i), messageSent(i)));
    // When
    StepVerifier.create(cmds.concatMap(cmdHandler)).expectNextCount(200).verifyComplete();
    // Then
    StepVerifier.create(eventStream.subscribe(0).take(200))
                .expectNext(expectedEvents.toJavaArray(Event[]::new))
                .verifyComplete();
  }

  private CreateUser createUser(int i) {
    return new CreateUser(commandId, Id.of("bob_id" + i), "bob_name" + i);
  }

  private MessageSent messageSent(int i) {
    return new MessageSent(eventId, Id.of("bob_id" + i), Id.of("alice_id" + i), "hello kafka" + i);
  }

  private SendMessage sendMessage(int i) {
    return new SendMessage(commandId, Id.of("bob_id" + i), Id.of("alice_id" + i), "hello kafka" + i);
  }

  private UserCreated userCreated(int i) {
    return new UserCreated(eventId, Id.of("bob_id" + i), "bob_name" + i);
  }
}
