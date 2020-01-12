package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.adapter.msgbus.kafka.KafkaMsgConsumer;
import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import dummy.Constants;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
// docker-compose -f docker-compose-single-broker.yml  up
// bin/kafka-topics.sh --list --bootstrap-server localhost:9092
// bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
// bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

public class KafkaConsumerTest {
  private static Map<String, String> config = HashMap.of("bootstrap.servers",
                                                         Constants.IP + ":" + Constants.KAFKA_PORT,
                                                         "enable.auto.commit",
                                                         "true",
                                                         "max.partition.fetch.bytes",
                                                         "135",
                                                         "group.id",
                                                         "group_1",
                                                         "heartbeat.interval.ms",
                                                         "3000",
                                                         "session.timeout.ms",
                                                         "6001",
                                                         "key.deserializer",
                                                         "org.apache.kafka.common.serialization.StringDeserializer",
                                                         "value.deserializer",
                                                         "org.apache.kafka.common.serialization.StringDeserializer");

  public static void main(String[] args) throws InterruptedException {
    MsgConsumer msgConsumer = new KafkaMsgConsumer(config, Schedulers.elastic(), Duration.ofSeconds(1));
    Flux<Try<ConsumeResponse>> topic = msgConsumer.consume(Constants.KAFKA_TOPIC, 0, 1).doOnNext(i -> {
      if (i.isSuccess()) {
        var msg = i.get().msg;
        System.out.println(msg.key + ":" + msg.value);
      } else {
        System.out.println(i.getCause().getMessage());
      }
    });
    topic.subscribe();
    //    StepVerifier.create(topic).expectNextCount(10).thenConsumeWhile(i -> i.isSuccess()).expectComplete().verify();
    System.out.println("DONE");
    Thread.sleep(60000);
  }
}
