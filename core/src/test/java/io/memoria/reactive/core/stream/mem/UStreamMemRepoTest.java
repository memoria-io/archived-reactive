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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UStreamMemRepoTest {
  private static final int N_ELEMENTS = 100000;
  private static final Map<String, Many<UMsg>> db = new ConcurrentHashMap<>();
  private static final UStreamMemRepo streamRepo = new UStreamMemRepo(db, 1000);
  private static final String SOME_TOPIC = "NODE_TOPIC";
  private static final int partition = 0;

  @BeforeEach
  void beforeEach() {
    db.clear();
  }

  @Test
  void publish() {
    // Given
    var msgs = List.range(0, N_ELEMENTS).map(i -> new UMsg(Id.of(i), "hello" + i));
    // When
    var pub = Flux.fromIterable(msgs).concatMap(msg -> streamRepo.publish(SOME_TOPIC, partition, msg));
    // Then
    StepVerifier.create(pub).expectNextCount(N_ELEMENTS).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    Flux.range(0, N_ELEMENTS).concatMap(this::publish).subscribe();
    Flux.range(N_ELEMENTS, N_ELEMENTS).concatMap(this::publish).delaySubscription(Duration.ofMillis(10)).subscribe();
    // When
    var sub = streamRepo.subscribe(SOME_TOPIC, partition, 0).map(UMsg::id).take(2 * N_ELEMENTS);
    var expected = List.range(0, 2 * N_ELEMENTS).map(Id::of).toJavaArray(Id[]::new);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private Mono<Id> publish(Integer i) {
    var uMsg = new UMsg(Id.of(i), "hello" + i);
    return streamRepo.publish(SOME_TOPIC, partition, uMsg);
  }
}
