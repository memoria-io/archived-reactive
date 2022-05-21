package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.pipeline.StatePipeline;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateUser;
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

class SocialNetworkPipelineTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Stream stream;
  private static final StatePipeline statePipeline;
  private static final SagaPipeline sagaPipeline;
  private static final Route route;
  private static final LogConfig logging;

  static {
    // Configs
    route = new Route("oldEventTopic", 0, "commandTopic", "eventTopic", 0, 1);
    logging = LogConfig.FINE;
    // Infra
    var cmdTp = new StreamConfig(route.commandTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    var eventTp = new StreamConfig(route.eventTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    stream = new MemStream(List.of(cmdTp, eventTp).toJavaList());
    // Pipeline
    statePipeline = new StatePipeline(stream,
                                      transformer,
                                      new Visitor(),
                                      new AccountStateDecider(),
                                      new AccountStateEvolver(),
                                      new AccountStateCompactor(),
                                      route,
                                      logging);
    sagaPipeline = new SagaPipeline(stream, transformer, new AccountSagaDecider(), route, logging);
  }

  @Test
  void pipeline() {
    // Given
    var bobId = StateId.of("bob");
    var createUserBob = new CreateUser(CommandId.randomUUID(), bobId, "bob");
    var janId = StateId.of("jan");
    var createUserJan = new CreateUser(CommandId.randomUUID(), janId, "jan");
    var sendMsgFromBobToJan = new CreateOutboundMsg(CommandId.randomUUID(), bobId, janId, "hello");
    var cmds = Flux.<Command>just(createUserBob, createUserJan, sendMsgFromBobToJan)
                   .map(SocialNetworkPipelineTest::toMsg);
    StepVerifier.create(stream.publish(cmds)).expectNextCount(3).verifyComplete();
    // When
    StepVerifier.create(statePipeline.run()).expectNextCount(3).expectTimeout(Duration.ofMillis(100)).verify();
    StepVerifier.create(sagaPipeline.run().delaySubscription(Duration.ofMillis(100)))
                .expectNextCount(1)
                .expectTimeout(Duration.ofMillis(1000))
                .verify();
  }

  private static Msg toMsg(Command command) {
    var body = transformer.blockingSerialize(command).get();
    return new Msg(route.commandTopic(), route.partition(), Id.of(UUID.randomUUID()), body);
  }
}