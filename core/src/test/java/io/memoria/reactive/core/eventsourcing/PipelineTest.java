package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.PipelineConfig.LogConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.StreamConfig;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.saga.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.state.StatePipeline;
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
  private static final PipelineConfig config;
  private static final StatePipeline statePipeline;
  private static final SagaPipeline sagaPipeline;

  static {
    // Infra
    stream = new MemStream(1000);
    transformer = new SerializableTransformer();
    // Configs
    var eventConfig = new StreamConfig("eventTopic", 0, 0, 1);
    var commandConfig = new StreamConfig("commandTopic", 0, 0, 1);
    var logConfig = LogConfig.DEFAULT;
    config = new PipelineConfig(eventConfig, commandConfig, logConfig);
    // Pipeline
    statePipeline = new StatePipeline(stream,
                                      transformer,
                                      new Visitor(),
                                      new UserStateDecider(),
                                      new UserStateEvolver(),
                                      config);
    sagaPipeline = new SagaPipeline(stream, transformer, new UserSagaDecider(), config);
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
    StepVerifier.create(statePipeline.run().take(3)).expectNextCount(3).verifyComplete();
    StepVerifier.create(sagaPipeline.run().delaySubscription(Duration.ofMillis(100)).take(5))
                .expectNextCount(5)
                .verifyComplete();
  }

  private static Msg toMsg(Command command) {
    var conf = config.commandConfig();
    var body = transformer.blockingSerialize(command).get();
    return new Msg(conf.topic(), conf.partition(), Id.of(UUID.randomUUID()), body);
  }
}
