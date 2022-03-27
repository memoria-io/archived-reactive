package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.UserSagaDecider;
import io.memoria.reactive.core.eventsourcing.UserStateDecider;
import io.memoria.reactive.core.eventsourcing.UserStateEvolver;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.stream.mem.MemStream;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

class PipelineTest {
  private static final Stream stream;
  private static final TextTransformer transformer;
  private static final StatePipeline statePipeline;
  private static final SagaPipeline sagaPipeline;
  private static final PipelineRoute PIPELINE_CONFIG;
  private static final PipelineLogConfig PIPELINE_LOGGING;

  static {
    // Infra
    stream = new MemStream(1000);
    transformer = new SerializableTransformer();
    // Configs
    PIPELINE_CONFIG = new PipelineRoute("commandTopic", "eventTopic", 0, 1);
    PIPELINE_LOGGING = PipelineLogConfig.DEFAULT;
    // Pipeline
    statePipeline = new StatePipeline(stream,
                                      transformer,
                                      new Visitor(),
                                      new UserStateDecider(),
                                      new UserStateEvolver(), PIPELINE_CONFIG, PIPELINE_LOGGING);
    sagaPipeline = new SagaPipeline(stream, transformer, new UserSagaDecider(), PIPELINE_CONFIG, PIPELINE_LOGGING);
  }

  @Test
  void pipeline() {
    // Given
    Id bobId = Id.of("bob");
    Id janId = Id.of("jan");
    var createUserBob = new CreateUser(bobId, "bob");
    var createUserJan = new CreateUser(janId, "jan");
    var sendMsgFromBobToJan = new CreateOutboundMsg(bobId, janId, "hello");
    var cmds = Flux.<Command>just(createUserBob, createUserJan, sendMsgFromBobToJan).map(PipelineTest::toMsg);
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
    return new Msg(PIPELINE_CONFIG.commandTopic(), PIPELINE_CONFIG.partition(), Id.of(UUID.randomUUID()), body);
  }
}
