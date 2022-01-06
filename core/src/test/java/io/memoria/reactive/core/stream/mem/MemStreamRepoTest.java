package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.Msg;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks.Many;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStreamRepoTest {
  private static final int N_ELEMENTS = 100000;
  private static final Map<String, Many<Msg>> db = new ConcurrentHashMap<>();
  private static final Map<String, AtomicInteger> sizes = new HashMap<>();
  private static final MemStreamRepo streamRepo = new MemStreamRepo(db, sizes, 1000);
  private static final String SOME_TOPIC = "NODE_TOPIC";

  @BeforeEach
  void beforeEach() {
    db.clear();
    sizes.clear();
    streamRepo.create(SOME_TOPIC).subscribe();
  }

  @Test
  void publish() {
    // Given
    var msgs = Flux.range(0, N_ELEMENTS).map(i -> new Msg(i, "hello" + i));
    // When
    var pub = msgs.flatMap(msg -> streamRepo.publish(SOME_TOPIC, msg));
    // Then
    var expected = List.range(0, N_ELEMENTS).map(i -> i + 1).toJavaArray(Integer[]::new);
    StepVerifier.create(pub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC)).expectNext(N_ELEMENTS).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    Flux.range(0, N_ELEMENTS).concatMap(this::publish).subscribe();
    Flux.range(N_ELEMENTS, N_ELEMENTS).concatMap(this::publish).delaySubscription(Duration.ofMillis(10)).subscribe();
    // When
    var sub = streamRepo.subscribe(SOME_TOPIC, 0).map(Msg::sKey).take(2 * N_ELEMENTS);
    var expected = List.range(0, 2 * N_ELEMENTS).toJavaArray(Integer[]::new);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC)).expectNext(2 * N_ELEMENTS).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private Mono<Integer> publish(Integer i) {
    return streamRepo.publish(SOME_TOPIC, new Msg(i, "hello" + i));
  }
}
