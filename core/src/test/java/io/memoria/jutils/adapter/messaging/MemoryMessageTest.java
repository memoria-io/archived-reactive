package io.memoria.jutils.adapter.messaging;

import io.memoria.jutils.adapter.messaging.memory.MsgReceiverMemoryRepo;
import io.memoria.jutils.adapter.messaging.memory.MsgSenderMemoryRepo;
import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.Response;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class MemoryMessageTest {
  private final MessageFilter mf = new MessageFilter("test_topic", 0, 0);
  private final int MSG_COUNT = 3;
  private final Flux<Message> msgs = Flux.interval(Duration.ofMillis(10))
                                         .map(i -> new Message("hello", i))
                                         .take(MSG_COUNT);

  @Test
  @DisplayName("Should consume messages correctly")
  public void consume() {
    var db = new HashMap<String, HashMap<Integer, Queue<Message>>>();
    db.put(mf.topic(), new HashMap<>());
    db.get(mf.topic()).put(mf.partition(), new LinkedList<>());
    db.get(mf.topic()).get(mf.partition()).addAll(Objects.requireNonNull(msgs.collectList().block()));
    var msgConsumer = new MsgReceiverMemoryRepo(db, mf);
    var consumed = msgConsumer.get().take(MSG_COUNT);
    StepVerifier.create(consumed.map(Message::id).map(Option::get))
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("Should publish messages correctly")
  public void publish() {
    var db = new HashMap<String, HashMap<Integer, Queue<Message>>>();
    var msgProducer = new MsgSenderMemoryRepo(db, mf);
    var published = msgProducer.apply(msgs).take(MSG_COUNT);

    StepVerifier.create(published.map(Response::id).map(Option::get))
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectComplete()
                .verify();
  }
}
