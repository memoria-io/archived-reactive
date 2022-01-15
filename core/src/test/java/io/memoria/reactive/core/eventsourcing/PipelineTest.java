package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMessage;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.state.StatePipeline;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.stream.mem.OStreamMemRepo;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks.Many;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class PipelineTest {
  private static final Logger log = LoggerFactory.getLogger(PipelineTest.class.getName());
  private static final String TOPIC = "SOME_TOPIC";
  // StateRepo
  private static final Map<Id, State> stateMap = new ConcurrentHashMap<>();
  // eventStreamRepo
  private static final Map<String, Many<OMsg>> topicStreams = new HashMap<>();
  private static final Map<String, AtomicInteger> topicSizes = new HashMap<>();
  private static final OStreamRepo oStreamRepo = new OStreamMemRepo(topicStreams, topicSizes, 1000);
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final EventStream eventStream = EventStream.defaultEventStream(TOPIC, oStreamRepo, transformer);

  @BeforeEach
  void beforeEach() {
    topicStreams.clear();
    topicSizes.clear();
    eventStream.createTopic().block();
  }

  @Test
  void applyCommand() {
    var pipeline = new StatePipeline(new Visitor(),
                                     stateMap,
                                     eventStream,
                                     new UserStateDecider(),
                                     new UserStateEvolver());

    // Given
    Id bobId = Id.of("0");
    Id janId = Id.of("1");
    var createUserBob = new CreateUser(bobId, "bob");
    var createUserJan = new CreateUser(janId, "jan");
    var sendMsgFromBobToJan = new CreateOutboundMessage(bobId, janId, "hello");
    // When
    var commands = Flux.just(createUserBob, createUserJan, sendMsgFromBobToJan);
    var decisionEvents = commands.flatMap(pipeline).doOnNext(debug("[Event]:"));
    // Then three events are created

    StepVerifier.create(decisionEvents).expectNextCount(3).verifyComplete();
    // plus two Saga Events  
    StepVerifier.create(eventStream.subscribe(0).take(3)).expectNextCount(3).verifyComplete();
  }

  private Consumer<Object> debug(String prefix) {
    return obj -> log.debug(prefix + obj);
  }
}
