package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.control.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;

import static io.vavr.control.Option.some;

public class InMemoryMsgTest {
  private final String TOPIC = "test_topic";
  private final String PARTITION = "0";
  private final int MSG_COUNT = 3;
  private final Flux<Msg> msgs = Flux.interval(Duration.ofMillis(10))
                                     .map(i -> new Msg("Msg number" + i))
                                     .take(MSG_COUNT);

  @Test
  @DisplayName("Should publish messages correctly")
  public void publish() {
    var db = new HashMap<String, HashMap<String, LinkedList<Msg>>>();
    var msgProducer = new InMemoryMsgProducer(db);
    var published = msgProducer.produce(TOPIC, PARTITION, msgs).take(MSG_COUNT);
    StepVerifier.create(published)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(pr -> pr.get().t.get().equals(2))
                .expectComplete()
                .verify();
    StepVerifier.create(msgProducer.close()).expectNextMatches(Try::isSuccess).expectComplete().verify();
  }

  @Test
  @DisplayName("Should consume messages correctly")
  public void consume() {
    var db = new HashMap<String, HashMap<String, LinkedList<Msg>>>();
    db.put(TOPIC, new HashMap<>());
    db.get(TOPIC).put(PARTITION, new LinkedList<>());
    db.get(TOPIC).get(PARTITION).addAll(msgs.collectList().block());
    var msgConsumer = new InMemoryMsgConsumer(db);
    var consumed = msgConsumer.consume(TOPIC, PARTITION, 0).take(MSG_COUNT);
    StepVerifier.create(consumed)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    StepVerifier.create(msgConsumer.close()).expectNextMatches(Try::isSuccess).expectComplete().verify();
  }
}
