package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

import static io.memoria.jutils.jkafka.KafkaUtils.init;
import static io.memoria.jutils.jkafka.KafkaUtils.last;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;

public class KafkaEventStore implements EventStore {
  private final KafkaConsumer<String, String> consumer;
  private final KafkaProducer<String, String> producer;
  private final Duration timeout;
  private final Scheduler scheduler;
  private final TextTransformer transformer;

  public KafkaEventStore(Map<String, Object> producerConfig,
                         Map<String, Object> consumerConfig,
                         Duration reqTimeout,
                         TextTransformer transformer,
                         Scheduler scheduler) {
    this.producer = new KafkaProducer<>(producerConfig);
    this.producer.initTransactions();
    this.consumer = new KafkaConsumer<>(consumerConfig);
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
    this.transformer = transformer;
  }

  @Override
  public Mono<Event> lastEvent(String topic, int partition) {
    return last(consumer, topic, partition, timeout).map(str -> transformer.deserialize(str, Event.class).get())
                                                    .subscribeOn(scheduler);
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, List<Event> events) {
    return Mono.fromRunnable(producer::beginTransaction)
               .thenMany(Flux.fromIterable(events))
               .flatMap(ev -> publishEvent(topic, partition, ev))
               .doOnNext(System.out::println)
               .then(Mono.fromRunnable(producer::commitTransaction))
               .then(Mono.just(events))
               .subscribeOn(scheduler);
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, long offset) {
    return Mono.fromRunnable(() -> init(consumer, topic, partition, offset, timeout))
               .thenMany(pollOnce(consumer, topic, partition, timeout).repeat())
               .map(msg -> transformer.deserialize(msg, Event.class).get())
               .subscribeOn(scheduler);
  }

  private Mono<Long> publishEvent(String topic, int partition, Event ev) {
    var value = transformer.serialize(ev).get();
    return KafkaUtils.sendRecord(producer, topic, partition, ev.aggId().value(), value, timeout)
                     .map(RecordMetadata::offset);
  }
}