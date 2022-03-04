package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

class MemStreamTest {
  private static final int N_ELEMENTS = 1000;
  private static final Stream STREAM = new MemStream(1000);
  private static final String TOPIC = "NODE_TOPIC";
  private static final int PARTITION = 0;

  @Test
  @Order(0)
  void publish() {
    // Given
    var msgs = createMsgs();
    // When
    var pub = STREAM.publish(msgs);
    // Then
    var expected = msgs.map(Msg::id).collectList().block();
    assert expected != null;
    StepVerifier.create(pub).expectNextSequence(expected).verifyComplete();
    StepVerifier.create(STREAM.size(TOPIC, PARTITION)).expectNext((long) N_ELEMENTS).verifyComplete();
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var msgs = createMsgs();
    STREAM.publish(msgs).delaySubscription(Duration.ofMillis(10)).subscribe();
    // When
    var sub = STREAM.subscribe(TOPIC, PARTITION, 0).map(Msg::id).take(2 * N_ELEMENTS);
    // Then
    StepVerifier.create(sub).expectNextCount(2 * N_ELEMENTS).verifyComplete();
    StepVerifier.create(STREAM.size(TOPIC, PARTITION)).expectNext(2L * N_ELEMENTS).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNextCount(2 * N_ELEMENTS).verifyComplete();
  }

  private Flux<Msg> createMsgs() {
    return Flux.range(0, N_ELEMENTS).map(i -> new Msg(TOPIC, PARTITION, Id.of(i), "hello" + i));
  }
}
