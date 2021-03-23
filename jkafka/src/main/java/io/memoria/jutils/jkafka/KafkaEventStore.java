package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.NoSuchElementException;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;
import static io.memoria.jutils.jkafka.KafkaUtils.createConsumer;
import static io.memoria.jutils.jkafka.KafkaUtils.createProducer;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;
import static io.memoria.jutils.jkafka.KafkaUtils.sendRecords;

public class KafkaEventStore implements EventStore {
  public static KafkaEventStore create(Map<String, Object> producerConfig,
                                       Map<String, Object> consumerConfig,
                                       String topic,
                                       int partition,
                                       TextTransformer transformer) {
    return create(producerConfig,
                  consumerConfig,
                  topic,
                  partition,
                  transformer,
                  Duration.ofMillis(1000),
                  Schedulers.boundedElastic());
  }

  public static KafkaEventStore create(Map<String, Object> producerConfig,
                                       Map<String, Object> consumerConfig,
                                       String topic,
                                       int partition,
                                       TextTransformer transformer,
                                       Duration reqTimeout,
                                       Scheduler scheduler) {
    return new KafkaEventStore(producerConfig, consumerConfig, topic, partition, transformer, reqTimeout, scheduler);
  }

  public final String topic;
  public final int partition;
  private final TextTransformer transformer;
  private final KafkaProducer<String, String> producer;
  private final Map<String, Object> consumerConfig;
  private final Duration timeout;
  private final Scheduler scheduler;

  private KafkaEventStore(Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig,
                          String topic,
                          int partition,
                          TextTransformer transformer,
                          Duration reqTimeout,
                          Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.producer = createProducer(producerConfig, topic, partition);
    this.consumerConfig = consumerConfig;
    this.transformer = transformer;
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<Event> last() {
    var lastMono = toMono(KafkaUtils.lastMessage(consumerConfig, topic, partition, timeout));
    return lastMono.onErrorResume(NoSuchElementException.class, t -> Mono.empty())
                   .map(msg -> transformer.deserialize(msg, Event.class))
                   .map(Try::get)
                   .subscribeOn(scheduler);
  }

  @Override
  public Mono<Long> publish(List<Event> events) {
    return Mono.fromCallable(() -> events.map(transformer::serialize).map(Try::get))
               .flatMap(msgs -> toMono(sendRecords(producer, topic, partition, msgs, timeout)))
               .subscribeOn(scheduler);
  }

  @Override
  public Flux<Event> subscribe(long offset) {
    return Mono.fromCallable(() -> createConsumer(consumerConfig, topic, partition, offset, timeout))
               .flatMapMany(consumer -> Mono.fromCallable(() -> pollOnce(consumer, topic, partition, timeout)).repeat())
               .concatMap(Flux::fromIterable)
               .map(msg -> transformer.deserialize(msg, Event.class))
               .map(Try::get)
               .subscribeOn(scheduler);
  }
}
