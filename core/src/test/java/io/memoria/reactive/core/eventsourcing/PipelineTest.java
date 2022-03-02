package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.saga.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.state.StatePipeline;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.mem.MemStream;
import io.memoria.reactive.core.text.SerializableTransformer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class PipelineTest {
  private static final Logger log = LoggerFactory.getLogger(PipelineTest.class.getName());
  private static final String CMD_TOPIC = "commands";
  private static final String EVENT_TOPIC = "events";
  private static final int nPartitions = 1;
  private static final int cmdSubPartition = 0;
  // Streams
  private static final EventStream eventStream;
  private static final CommandStream commandStream;

  static {
    var transformer = new SerializableTransformer();
    var streamRepo = new MemStream(1000);
    eventStream = EventStream.defaultEventStream(EVENT_TOPIC, nPartitions, streamRepo, transformer);
    commandStream = CommandStream.defaultCommandStream(CMD_TOPIC,
                                                       nPartitions,
                                                       cmdSubPartition,
                                                       streamRepo,
                                                       transformer);
  }

  @Test
  void pipeline() {
    var statePipeline = new StatePipeline(new Visitor(),
                                          new ConcurrentHashMap<>(),
                                          commandStream,
                                          eventStream,
                                          new UserStateDecider(),
                                          new UserStateEvolver());
    var sagaPipeline = new SagaPipeline(eventStream, commandStream, new UserSagaDecider());
    // Given
    Id bobId = Id.of("bob");
    Id janId = Id.of("jan");
    var createUserBob = new CreateUser(bobId, "bob");
    var createUserJan = new CreateUser(janId, "jan");
    var sendMsgFromBobToJan = new CreateOutboundMsg(bobId, janId, "hello");
    var cmds = Flux.<Command>just(createUserBob, createUserJan, sendMsgFromBobToJan);
    StepVerifier.create(commandStream.publish(cmds)).expectNextCount(3).verifyComplete();
    // When
    statePipeline.run(0).subscribe(debug("[State]:"));
    sagaPipeline.run(0).delaySubscription(Duration.ofMillis(100)).subscribe(debug("[Command]:"));
    StepVerifier.create(commandStream.subscribe(0).take(5)).expectNextCount(5).verifyComplete();
    StepVerifier.create(eventStream.subscribe(0).take(5)).expectNextCount(5).verifyComplete();
  }

  private Consumer<Object> debug(String prefix) {
    return obj -> log.debug(prefix + obj);
    //        return obj -> System.out.println(prefix + obj);
  }
}
