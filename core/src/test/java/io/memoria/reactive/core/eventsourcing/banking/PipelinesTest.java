package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.core.eventsourcing.banking.state.Account;
import io.memoria.reactive.core.eventsourcing.banking.state.Visitor;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.eventsourcing.pipeline.state.StateDomain;
import io.memoria.reactive.core.eventsourcing.pipeline.state.StatePipeline;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.stream.mem.MemStream;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

class PipelinesTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Duration timeout = Duration.ofMillis(300);
  // pipeline sharding
  private static final int oldPartitions = 3;
  private static final int newPartitions = 5;
  private static final String commandTopic = "commandTopic";
  private static final String oldEventTopic = "oldEventTopic";
  public static final String newEventTopic = "newEventTopic";
  // Pipelines
  private final Stream stream;
  private final List<StatePipeline<Account, AccountCommand, AccountEvent>> oldPipeline;
  private final List<StatePipeline<Account, AccountCommand, AccountEvent>> pipeline1;

  PipelinesTest() {
    // Pipelines
    var oldRoutes = List.range(0, oldPartitions).map(PipelinesTest::odlRoute0);
    var newRoutes = List.range(0, newPartitions).map(PipelinesTest::route1);
    stream = new MemStream(newRoutes.get().commandConfig().withHistory(100),
                           newRoutes.get().prevEventConfig(),
                           newRoutes.get().eventConfig());
    oldPipeline = oldRoutes.map(this::createPipeline);
    pipeline1 = newRoutes.map(this::createPipeline);
  }

  @Test
  void sharding() {
    // Given published commands
    int personsCount = 10;
    int nameChanges = 2;
    int eventCount = personsCount + (personsCount * nameChanges);

    // When simple pipeline is activated
    runOldPipeline(personsCount, nameChanges, eventCount);

    // Then events are published to old pipeline topic, with number of totalPartitions = prevPartitions
    var oldEvents = Flux.range(0, oldPartitions).flatMap(i -> accountCreatedStream(oldEventTopic, i));
    StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);

    // And When
    StepVerifier.create(Flux.merge(pipeline1.map(StatePipeline::run)))
                .expectNextCount(eventCount)
                .verifyTimeout(timeout);

    // Then events are published to the new pipeline topic, with number of totalPartitions = totalPartitions,
    var newEvents = Flux.range(0, newPartitions).flatMap(i -> accountCreatedStream(newEventTopic, i));
    StepVerifier.create(newEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  }

  @Test
  void reduction() {
    // Given published commands
    int personsCount = 10;
    int nameChanges = 2;
    int eventCount = personsCount + (personsCount * nameChanges);

    // When simple pipeline is activated
    runOldPipeline(personsCount, nameChanges, eventCount);

    // Then events are published to old pipeline topic, with number of totalPartitions = prevPartitions
    var oldEvents = Flux.range(0, oldPartitions).flatMap(i -> accountCreatedStream(oldEventTopic, i));
    StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);

    // And When new pipelines are run with reduction
    StepVerifier.create(Flux.merge(pipeline1.map(StatePipeline::runReduced)))
                .expectNextCount(personsCount)
                .verifyTimeout(timeout);

    /*
     * Then events are published to the new pipeline topic, with number of totalPartitions = totalPartitions,
     * and only one event per user
     */
    var newEvents = Flux.range(0, newPartitions).flatMap(i -> accountCreatedStream(newEventTopic, i));
    StepVerifier.create(newEvents).expectNextCount(personsCount).verifyTimeout(timeout);
  }

  private void runOldPipeline(int personsCount, int nameChanges, int eventCount) {
    stream.publish(DataSet.scenario(personsCount, nameChanges).map(PipelinesTest::toMsg))
          .delaySubscription(Duration.ofMillis(100))
          .subscribe();
    StepVerifier.create(Flux.merge(oldPipeline.map(StatePipeline::run)))
                .expectNextCount(eventCount)
                .verifyTimeout(timeout);
  }

  private StatePipeline<Account, AccountCommand, AccountEvent> createPipeline(Route route) {
    return new StatePipeline<>(stateDomain(), stream, transformer, route, LogConfig.FINE);
  }

  private StateDomain<Account, AccountCommand, AccountEvent> stateDomain() {
    return new StateDomain<>(Account.class,
                             AccountCommand.class,
                             AccountEvent.class,
                             new Visitor(),
                             new AccountStateDecider(),
                             new AccountStateEvolver(),
                             new AccountStateReducer());
  }

  private Flux<AccountEvent> accountCreatedStream(String topic, int i) {
    return stream.subscribe(topic, i, 0).concatMap(msg -> transformer.deserialize(msg.value(), AccountEvent.class));
    //                 .doOnNext(e -> System.out.printf("p(%d)-%s%n", i, e));
  }

  private static Route odlRoute0(int partition) {
    return new Route(commandTopic, partition, "dummy", 0, oldEventTopic, oldPartitions);
  }

  private static Route route1(int partition) {
    return new Route(commandTopic, partition, oldEventTopic, oldPartitions, newEventTopic, newPartitions);
  }

  private static Msg toMsg(Command command) {
    var body = transformer.blockingSerialize(command).get();
    return new Msg(commandTopic, command.partition(oldPartitions), Id.of(UUID.randomUUID()), body);
  }
}
