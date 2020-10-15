package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.messaging.MessageFilter;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

public record KafkaConfig(int timeout, Map<String, Object> producer, Map<String, Object> consumer) {
  public KafkaReceiver createReceiver(MessageFilter messageFilter, Scheduler scheduler) {
    var kafkaConsumer = new KafkaConsumer<String, String>(consumer);
    return new KafkaReceiver(kafkaConsumer, messageFilter, scheduler, Duration.ofMillis(timeout));
  }

  public KafkaSender createSender(MessageFilter messageFilter, Scheduler scheduler) {
    var kafkaProducer = new KafkaProducer<String, String>(producer);
    return new KafkaSender(kafkaProducer, messageFilter, scheduler, Duration.ofMillis(timeout));
  }
}
