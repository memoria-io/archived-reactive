package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.UMsg;
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

public class UStreamMemRepoTest {
  private static final int N_ELEMENTS = 100000;
  private static final Map<String, Many<UMsg>> db = new ConcurrentHashMap<>();
  private static final Map<String, AtomicInteger> sizes = new HashMap<>();
  private static final UStreamMemRepo streamRepo = new UStreamMemRepo(db, sizes, 1000);
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
    var msgs = List.range(0, N_ELEMENTS).map(i -> new UMsg(Id.of(i), "hello" + i));
    // When
    var pub = Flux.fromIterable(msgs).flatMap(msg -> streamRepo.publish(SOME_TOPIC, msg));
    // Then
    var expected = msgs.toJavaArray(UMsg[]::new);
    StepVerifier.create(pub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC)).expectNext(N_ELEMENTS).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    Flux.range(0, N_ELEMENTS).concatMap(this::publish).subscribe();
    Flux.range(N_ELEMENTS, N_ELEMENTS).concatMap(this::publish).delaySubscription(Duration.ofMillis(10)).subscribe();
    // When
    var sub = streamRepo.subscribe(SOME_TOPIC, 0).map(UMsg::id).take(2 * N_ELEMENTS);
    var expected = List.range(0, 2 * N_ELEMENTS).map(Id::of).toJavaArray(Id[]::new);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
    StepVerifier.create(streamRepo.size(SOME_TOPIC)).expectNext(2 * N_ELEMENTS).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private Mono<UMsg> publish(Integer i) {
    return streamRepo.publish(SOME_TOPIC, new UMsg(Id.of(i), "hello" + i));
  }
}
