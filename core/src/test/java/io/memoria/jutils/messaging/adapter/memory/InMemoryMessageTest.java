package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.Response;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;

import static io.vavr.API.Some;
import static java.lang.String.valueOf;

public class InMemoryMessageTest {
  private final MessageFilter mf = new MessageFilter("test_topic", 0, 0);
  private final int MSG_COUNT = 3;
  private final Flux<Message> msgs = Flux.interval(Duration.ofMillis(10))
                                         .map(i -> new Message("hello_" + i).withId(i))
                                         .take(MSG_COUNT);

  @Test
  @DisplayName("Should consume messages correctly")
  public void consume() {
    var db = new HashMap<String, HashMap<Integer, LinkedList<Message>>>();
    db.put(mf.topic(), new HashMap<>());
    db.get(mf.topic()).put(mf.partition(), new LinkedList<>());
    db.get(mf.topic()).get(mf.partition()).addAll(msgs.collectList().block());
    var msgConsumer = new InMemoryMsgReceiver(db, mf);
    var consumed = msgConsumer.get().take(MSG_COUNT);
    StepVerifier.create(consumed.map(Message::id))
                .expectNext(Some(valueOf(0L)))
                .expectNext(Some(valueOf(1L)))
                .expectNext(Some(valueOf(2L)))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("Should publish messages correctly")
  public void publish() {
    var db = new HashMap<String, HashMap<Integer, LinkedList<Message>>>();
    var msgProducer = new InMemoryMsgSender(db, mf);
    var published = msgProducer.apply(msgs).take(MSG_COUNT);

    StepVerifier.create(published.map(Response::value).map(Option::get))
                .expectNext(valueOf(0L))
                .expectNext(valueOf(1L))
                .expectNext(valueOf(2L))
                .expectComplete()
                .verify();
  }
}
