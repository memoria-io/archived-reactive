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
import io.memoria.reactive.core.stream.mem.MemStreamConfig;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

class ShardingPipelineTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Duration timeout = Duration.ofMillis(300);
  // topics config, with ephemeral command topic
  private static final MemStreamConfig cmdTp = new MemStreamConfig("commandsTopic", 100, 2);
  private static final MemStreamConfig oldEventTopic = new MemStreamConfig("oldEventTopic", 100, 100);
  private static final MemStreamConfig newEventTopic = new MemStreamConfig("newEventTopic", 100, 100);
  private static final int prevPartitions = 3;
  private static final int totalPartitions = 5;
  // Pipelines
  private final Stream stream;
  private final List<StatePipeline<Account, AccountCommand, AccountEvent>> oldPipelines;
  private final List<StatePipeline<Account, AccountCommand, AccountEvent>> newPipelines;

  ShardingPipelineTest() {
    // Infra
    stream = new MemStream(Set.of(cmdTp, oldEventTopic, newEventTopic));
    // Pipelines
    oldPipelines = List.range(0, prevPartitions).map(this::simpleRoute).map(this::createPipeline);
    newPipelines = List.range(0, totalPartitions).map(this::shardedRoute).map(this::createPipeline);
  }

  @Test
  void sharding() {
    // Given published commands
    int personsCount = 10;
    int nameChanges = 2;
    int eventCount = personsCount + (personsCount * nameChanges);

    // When simple pipeline is activated
    stream.publish(DataSet.scenario(personsCount, nameChanges).map(ShardingPipelineTest::toMsg))
          .delaySubscription(Duration.ofMillis(100))
          .subscribe();
    StepVerifier.create(Flux.merge(oldPipelines.map(StatePipeline::run)))
                .expectNextCount(eventCount)
                .verifyTimeout(timeout);

    // Then events are published to old pipeline topic, with number of partitions = prevPartitions  
    var oldEvents = Flux.range(0, prevPartitions).flatMap(i -> accountCreatedStream(oldEventTopic.name(), i));
    StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);

    // And When
    StepVerifier.create(Flux.merge(newPipelines.map(StatePipeline::run)))
                .expectNextCount(eventCount)
                .verifyTimeout(timeout);

    // Then events are published to the new pipeline topic, with number of partitions = totalPartitions, 
    var newEvents = Flux.range(0, totalPartitions).flatMap(i -> accountCreatedStream(newEventTopic.name(), i));
    StepVerifier.create(newEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  }

  @Test
  void reduction() {
    // Given published commands
    int personsCount = 10;
    int nameChanges = 2;
    int eventCount = personsCount + (personsCount * nameChanges);

    // When simple pipeline is activated
    stream.publish(DataSet.scenario(personsCount, nameChanges).map(ShardingPipelineTest::toMsg))
          .delaySubscription(Duration.ofMillis(100))
          .subscribe();
    StepVerifier.create(Flux.merge(oldPipelines.map(StatePipeline::run)))
                .expectNextCount(eventCount)
                .verifyTimeout(timeout);

    // Then events are published to old pipeline topic, with number of partitions = prevPartitions  
    var oldEvents = Flux.range(0, prevPartitions).flatMap(i -> accountCreatedStream(oldEventTopic.name(), i));
    StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);

    // And When new pipelines are run with reduction
    StepVerifier.create(Flux.merge(newPipelines.map(StatePipeline::runReduced)))
                .expectNextCount(personsCount)
                .verifyTimeout(timeout);

    /*
     * Then events are published to the new pipeline topic, with number of partitions = totalPartitions,
     * and only one event per user
     */
    var newEvents = Flux.range(0, totalPartitions).flatMap(i -> accountCreatedStream(newEventTopic.name(), i));
    StepVerifier.create(newEvents).expectNextCount(personsCount).verifyTimeout(timeout);
  }

  private Route shardedRoute(int partition) {
    return new Route(oldEventTopic.name(),
                     prevPartitions,
                     cmdTp.name(),
                     newEventTopic.name(),
                     partition,
                     totalPartitions);
  }

  private Route simpleRoute(int partition) {
    return new Route(cmdTp.name(), oldEventTopic.name(), partition, prevPartitions);
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

  private static Msg toMsg(Command command) {
    var body = transformer.blockingSerialize(command).get();
    return new Msg(cmdTp.name(), command.partition(prevPartitions), Id.of(UUID.randomUUID()), body);
  }
}
