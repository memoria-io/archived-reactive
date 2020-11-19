//package io.memoria.jutils.eventsourcing.event;
//
//import io.memoria.jutils.core.messaging.MessageLocation;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import reactor.core.scheduler.Scheduler;
//
//import java.time.Duration;
//import java.util.Map;
//
//public record KafkaConfig(int timeout, Map<String, Object> producer, Map<String, Object> consumer) {
//  public KafkaReceiver createReceiver(MessageLocation messageLocation, Scheduler scheduler) {
//    var kafkaConsumer = new KafkaConsumer<String, String>(consumer);
//    return new KafkaReceiver(kafkaConsumer, messageLocation, scheduler, Duration.ofMillis(timeout));
//  }
//
//  public KafkaSender createSender(MessageLocation messageLocation, Scheduler scheduler) {
//    var kafkaProducer = new KafkaProducer<String, String>(producer);
//    return new KafkaSender(kafkaProducer, messageLocation, scheduler, Duration.ofMillis(timeout));
//  }
//}
