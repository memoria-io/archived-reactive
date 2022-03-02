package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.saga.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.state.StatePipeline;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.mem.OStreamMemRepo;
import io.memoria.reactive.core.stream.mem.UStreamMemRepo;
import io.memoria.reactive.core.text.SerializableTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class PipelineTest {
  private static final Logger log = LoggerFactory.getLogger(PipelineTest.class.getName());
  private static final String TOPIC = "SOME_TOPIC";
  private static final int nPartitions = 1;
  private static final int cmdSubPartition = 0;
  // Streams
  private static final EventStream eventStream;
  private static final CommandStream commandStream;

  static {
    var transformer = new SerializableTransformer();
    var oStreamRepo = new OStreamMemRepo(new HashMap<>(), new HashMap<>(), 1000);
    var uStreamRepo = new UStreamMemRepo(new HashMap<>(), 1000);
    eventStream = EventStream.defaultEventStream(TOPIC, nPartitions, oStreamRepo, transformer);
    commandStream = CommandStream.defaultCommandStream(TOPIC, nPartitions, cmdSubPartition, uStreamRepo, transformer);
  }

  @BeforeEach
  void beforeEach() {}

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
    var cmdPub = Flux.just(createUserBob, createUserJan, sendMsgFromBobToJan).concatMap(commandStream::publish);
    StepVerifier.create(cmdPub).expectNextCount(3).verifyComplete();
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
