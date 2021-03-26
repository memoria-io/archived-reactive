package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jkafka.data.user.User;
import io.memoria.jutils.jkafka.data.user.User.Visitor;
import io.memoria.jutils.jkafka.data.user.UserCommand;
import io.memoria.jutils.jkafka.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jkafka.data.user.UserDecider;
import io.memoria.jutils.jkafka.data.user.UserEvent.UserCreated;
import io.memoria.jutils.jkafka.data.user.UserEvolver;
import io.memoria.jutils.jkafka.data.user.UserTextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

class CommandHandlerTest {

  private final CommandHandler<User, UserCommand> cmdHandler;
  private final EventStore eventStore;

  CommandHandlerTest() {
    String topic = "Topic_" + new Random().nextInt(1000);
    KafkaAdmin.create().createTopic(topic, 2, 1).block();
    this.eventStore = KafkaEventStore.create(TestConfigs.producerConf,
                                             TestConfigs.consumerConf,
                                             topic,
                                             0,
                                             new UserTextTransformer());
    cmdHandler = new CommandHandler<>(new Visitor(), eventStore, new UserDecider(() -> Id.of(1)), new UserEvolver());
  }

  @Test
  void handleCommands2() {
    // Given
    var commands = Flux.range(0, 100).map(i -> new CreateUser(Id.of(i), Id.of("bob_id" + i), "bob_name" + i));
    var expectedEvents = List.range(0, 100)
                             .map(i -> (Event) new UserCreated(Id.of(1), Id.of("bob_id" + i), "bob_name" + i));
    // When
    StepVerifier.create(commands.concatMap(cmdHandler)).expectNextCount(100).verifyComplete();
    // Then
    StepVerifier.create(eventStore.subscribe(0).take(100))
                .expectNext(expectedEvents.toJavaArray(Event[]::new))
                .verifyComplete();
  }
}
