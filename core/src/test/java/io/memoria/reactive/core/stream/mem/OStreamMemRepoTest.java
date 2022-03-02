package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.OMsg;
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
import java.util.concurrent.atomic.AtomicLong;

public class OStreamMemRepoTest {
  private static final long N_ELEMENTS = 10000;
  private static final Map<String, Many<OMsg>> db = new ConcurrentHashMap<>();
  private static final Map<String, AtomicLong> sizes = new HashMap<>();
  private static final OStreamMemRepo streamRepo = new OStreamMemRepo(db, sizes, 1000);
  private static final String SOME_TOPIC = "NODE_TOPIC";
  private static final int partition = 0;

  @BeforeEach
  void beforeEach() {
    db.clear();
    sizes.clear();
  }

  @Test
  void publish() {
    // Given
    var msgs = List.range(0, N_ELEMENTS).map(i -> new OMsg(i, "hello" + i));
    // When
    var pub = Flux.fromIterable(msgs).flatMap(msg -> streamRepo.publish(SOME_TOPIC, partition, msg));
    // Then
    var expected = msgs.map(OMsg::sKey).toJavaArray(Long[]::new);
    StepVerifier.create(pub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC, partition)).expectNext(N_ELEMENTS).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    Flux.range(0, (int) N_ELEMENTS).concatMap(this::publish).subscribe();
    Flux.range((int) N_ELEMENTS, (int) N_ELEMENTS)
        .concatMap(this::publish)
        .delaySubscription(Duration.ofMillis(10))
        .subscribe();
    // When
    var sub = streamRepo.subscribe(SOME_TOPIC, partition, 0).map(OMsg::sKey).take(2 * N_ELEMENTS);
    var expected = List.range(0, 2 * N_ELEMENTS).toJavaArray(Long[]::new);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC, partition)).expectNext(2 * N_ELEMENTS).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private Mono<Long> publish(long i) {
    var oMsg = new OMsg(i, "hello" + i);
    return streamRepo.publish(SOME_TOPIC, partition, oMsg);
  }
}
