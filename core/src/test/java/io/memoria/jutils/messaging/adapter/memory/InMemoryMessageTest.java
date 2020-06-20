package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.core.domain.port.crud.Storable;
import io.memoria.jutils.messaging.domain.Message;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.String.valueOf;

public class InMemoryMessageTest {
  private final String TOPIC = "test_topic";
  private final String PARTITION = "0";
  private final int MSG_COUNT = 3;
  private final Flux<Message> msgs = Flux.interval(Duration.ofMillis(10))
                                         .map(i -> new Message(i + "", "hello_" + i))
                                         .take(MSG_COUNT);

  @Test
  @DisplayName("Should publish messages correctly")
  public void publish() {
    var db = new HashMap<String, HashMap<String, LinkedList<Message>>>();
    var msgProducer = new InMemoryMsgSender(db);
    var published = msgProducer.send(TOPIC, PARTITION, msgs).take(MSG_COUNT);

    StepVerifier.create(published.filter(Option::isDefined).map(Option::get).map(Storable::id))
                .expectNext(valueOf(0L))
                .expectNext(valueOf(1L))
                .expectNext(valueOf(2L))
                .expectComplete()
                .verify();
    StepVerifier.create(msgProducer.close()).expectComplete().verify();
  }

  @Test
  @DisplayName("Should consume messages correctly")
  public void consume() {
    var db = new HashMap<String, HashMap<String, LinkedList<Message>>>();
    db.put(TOPIC, new HashMap<>());
    db.get(TOPIC).put(PARTITION, new LinkedList<>());
    db.get(TOPIC).get(PARTITION).addAll(msgs.collectList().block());
    var msgConsumer = new InMemoryMsgReceiver(db);
    var consumed = msgConsumer.receive(TOPIC, PARTITION, 0).take(MSG_COUNT);
    StepVerifier.create(consumed.map(Storable::id))
                .expectNext(valueOf(0L))
                .expectNext(valueOf(1L))
                .expectNext(valueOf(2L))
                .expectComplete()
                .verify();
    StepVerifier.create(msgConsumer.close()).expectComplete().verify();
  }
}
