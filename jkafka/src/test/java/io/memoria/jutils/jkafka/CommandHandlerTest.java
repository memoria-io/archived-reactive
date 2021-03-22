package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jkafka.data.user.User;
import io.memoria.jutils.jkafka.data.user.User.Visitor;
import io.memoria.jutils.jkafka.data.user.UserCommand;
import io.memoria.jutils.jkafka.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jkafka.data.user.UserDecider;
import io.memoria.jutils.jkafka.data.user.UserEvolver;
import io.memoria.jutils.jkafka.data.user.UserTextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandHandlerTest {

  private final ConcurrentHashMap<Id, User> stateStore;
  private final CommandHandler<User, UserCommand> cmdHandler;

  CommandHandlerTest() {
    // Setup
    int PARTITION = 0;
    String TOPIC = "Topic_" + new Random().nextInt(1000);
    var admin = new KafkaAdmin("localhost:9092", Duration.ofMillis(1000), Schedulers.boundedElastic());
    var eventStore = new KafkaEventStore(TestConfigs.producerConf,
                                         TestConfigs.consumerConf,
                                         TOPIC,
                                         PARTITION,
                                         new UserTextTransformer(),
                                         Duration.ofMillis(1000),
                                         Schedulers.boundedElastic());
    stateStore = CommandHandler.buildState(eventStore, new UserEvolver()).block();
    cmdHandler = new CommandHandler<>(new Visitor(),
                                      stateStore,
                                      eventStore,
                                      new UserDecider(() -> Id.of(1)),
                                      new UserEvolver());
  }

  @Test
  void handleCommands() {
    // When
    Flux.range(0, 100)
        .concatMap(i -> cmdHandler.apply(new CreateUser(Id.of(i), Id.of("bob_id" + i), "bob_name" + i)))
        .blockLast();
    // Then
    List.range(0, 100)
        .forEach(i -> assertEquals(new User.Account("bob_name" + i), stateStore.get(Id.of("bob_id" + i))));
  }
}
