package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.stream.mem.MemStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

class MemStreamRepoTest {

  @Test
  void publish() {
    // Given
    var streamDB = new ArrayList<Msg>();
    var streamRepo = new MemStream(streamDB);
    var msgs = createMsgs();
    // When
    var publish = streamRepo.publish(Flux.fromIterable(msgs));
    // Then
    StepVerifier.create(publish).expectNext(msgs.toArray(Msg[]::new)).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    var msgs = createMsgs();
    var streamRepo = new MemStream(msgs);
    var expectedEvents = msgs.toArray(Msg[]::new);
    // When
    var subscribe = streamRepo.subscribe(0);
    // Then
    StepVerifier.create(subscribe).expectNext(expectedEvents).verifyComplete();
  }

  private List<Msg> createMsgs() {
    return io.vavr.collection.List.range(0, 100).map(i -> Msg.of(Id.of(i), "hello:" + i)).toJavaList();
  }
}
