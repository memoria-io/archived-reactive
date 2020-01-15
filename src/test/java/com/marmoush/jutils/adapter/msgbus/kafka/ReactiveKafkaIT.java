package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Random;

import static io.vavr.control.Option.some;

public class ReactiveKafkaIT {
  private final Map<String, Object> config = YamlUtils.parseYamlResource("kafka.yaml").get();
  private final String PARTITION = "0";
  private final int MSG_COUNT = 3;

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    @SuppressWarnings("unchecked")
    var publisherConf = (LinkedHashMap<String, Object>) config.get("publisher").get();

    var kafkaProducer = new KafkaProducer<String, String>(publisherConf);
    var msgProducer = new KafkaMsgProducer(kafkaProducer, Schedulers.elastic(), Duration.ofSeconds(1));

    @SuppressWarnings("unchecked")
    var consumerConf = (LinkedHashMap<String, Object>) config.get("consumer").get();
    var kafkaConsumer = new KafkaConsumer<String, String>(consumerConf);
    var msgConsumer = new KafkaMsgConsumer(kafkaConsumer, Schedulers.elastic(), Duration.ofSeconds(1));

    var msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg("Msg number" + i, some(i + ""))).take(MSG_COUNT);
    var publisher = msgProducer.produce(TOPIC, PARTITION, msgs);
    var consumer = msgConsumer.consume(TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(publisher)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    StepVerifier.create(consumer)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(pr -> pr.get().msg.pkey.equals(some("2")))
                .expectComplete()
                .verify();
  }
}
