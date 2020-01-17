package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

import static io.vavr.control.Option.some;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReactivePulsarIT {

  private final YamlConfigMap config;
  private Flux<Msg> msgs;

  public ReactivePulsarIT() {
    config = YamlUtils.parseYamlResource("pulsar.yaml").get();
    msgs = Flux.interval(Duration.ofMillis(10)).log().map(i -> new Msg("Msg number" + i, some(i + "")));
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume() throws PulsarClientException {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";

    var producer = new PulsarMsgProducer(config);
    StepVerifier.create(producer.produce(TOPIC, PARTITION, msgs.take(3)))
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    producer.close().subscribe();
    var consumer = new PulsarMsgConsumer(config);
    var c = consumer.consume(TOPIC, PARTITION, 0);
    StepVerifier.create(c).expectNextCount(2).expectNextMatches(s -> {
      print(s.get().t.get());
      return true;
    }).thenCancel().verify();
    consumer.close().subscribe();
  }

  public static void print(Message<String> s) {
    System.out.println("topicname:" + s.getTopicName());
    System.out.println("orderingKey:" + s.hasOrderingKey());
    System.out.println("key:" + s.getKey());
    System.out.println("data:" + s.getData());
    System.out.println("sequenceId:" + s.getSequenceId());
    System.out.println("messageID:" + s.getMessageId());
    System.out.println("isReplicated:" + s.isReplicated());
    System.out.println("--------------------");
  }
}

