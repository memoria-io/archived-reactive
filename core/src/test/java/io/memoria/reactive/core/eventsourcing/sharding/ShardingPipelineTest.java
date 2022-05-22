package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDomain;
import io.memoria.reactive.core.eventsourcing.pipeline.StatePipeline;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.stream.mem.MemStream;
import io.memoria.reactive.core.stream.mem.StreamConfig;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

class ShardingPipelineTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Duration timeout = Duration.ofMillis(300);
  // topics config, with ephemeral command topic
  private static final StreamConfig cmdTp = new StreamConfig("commandsTopic", 100, 2);
  private static final StreamConfig oldEventTopic = new StreamConfig("oldEventTopic", 100, 100);
  private static final StreamConfig newEventTopic = new StreamConfig("newEventTopic", 100, 100);
  private static final int prevPartitions = 3;
  private static final int totalPartitions = 5;
  // Pipelines
  private final Stream stream;
  private final List<StatePipeline<Account, AccountEvent, AccountCommand>> oldPipelines;
  private final List<StatePipeline<Account, AccountEvent, AccountCommand>> newPipelines;

  ShardingPipelineTest() {
    // Infra
    stream = new MemStream(List.of(cmdTp, oldEventTopic, newEventTopic).toJavaList());
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
    stream.publish(DataSet.personScenario(personsCount, nameChanges).map(ShardingPipelineTest::toMsg))
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
    stream.publish(DataSet.personScenario(personsCount, nameChanges).map(ShardingPipelineTest::toMsg))
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

  private StatePipeline<Account, AccountEvent, AccountCommand> createPipeline(Route route) {
    return new StatePipeline<>(stateDomain(), stream, transformer, route, LogConfig.FINE);
  }

  private StateDomain<Account, AccountEvent, AccountCommand> stateDomain() {
    return new StateDomain<>(Account.class,
                             AccountEvent.class,
                             AccountCommand.class,
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
