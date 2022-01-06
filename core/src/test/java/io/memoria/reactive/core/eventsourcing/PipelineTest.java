package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.mem.MemStateRepo;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.StreamRepo;
import io.memoria.reactive.core.stream.mem.MemStreamRepo;
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
  private static final Map<String, State> db = new ConcurrentHashMap<>();
  private static final StateRepo stateRepo = new MemStateRepo(db);
  // eventStreamRepo
  private static final Map<String, Many<Msg>> topicStreams = new HashMap<>();
  private static final Map<String, AtomicInteger> topicSizes = new HashMap<>();
  private static final StreamRepo eventStreamRepo = new MemStreamRepo(topicStreams, topicSizes, 1000);
  // Transformer
  private static final TextTransformer transformer = new SerializableTransformer();

  @BeforeEach
  void beforeEach() {
    topicStreams.clear();
    topicSizes.clear();
    eventStreamRepo.create(TOPIC).block();
  }

  @Test
  void applyCommand() {
    var pipeline = new Pipeline(TOPIC,
                                new Visitor(Id.of(0)),
                                stateRepo,
                                eventStreamRepo,
                                new UserDecider(),
                                new UserEvolver(),
                                new UserSaga(),
                                transformer);
    pipeline.evolve(0).doOnNext(debug("[State]:")).subscribe();
    pipeline.saga(0).doOnNext(debug("[EventSaga]:")).subscribe();

    // Given
    Id bobId = Id.of("0");
    Id janId = Id.of("1");
    var createUserBob = new UserCommand.CreateUser(bobId, "bob");
    var createUserJan = new UserCommand.CreateUser(janId, "jan");
    var sendMsgFromBobToJan = new UserCommand.SendMessage(bobId, janId, "hello");
    // When
    var commands = Flux.just(createUserBob, createUserJan, sendMsgFromBobToJan);
    var decisionEvents = commands.flatMap(pipeline::decide).doOnNext(debug("[Event]:"));
    // Then three events are created
    StepVerifier.create(decisionEvents).expectNextCount(3).verifyComplete();
    // plus two Saga Events  
    StepVerifier.create(eventStreamRepo.subscribe(TOPIC).take(5)).expectNextCount(5).verifyComplete();
  }

  private Consumer<Object> debug(String prefix) {
    return obj -> log.debug(prefix + obj);
  }
}
