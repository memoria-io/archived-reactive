package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;
import static io.memoria.jutils.jkafka.KafkaUtils.createConsumer;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;
import static io.memoria.jutils.jkafka.KafkaUtils.sendRecords;

public class KafkaEventStore implements EventStore {
  public final String topic;
  public final int partition;
  public final String TRANSACTION_ID;
  private final TextTransformer transformer;
  private final KafkaProducer<String, String> producer;
  private final Map<String, Object> consumerConfig;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaEventStore(Map<String, Object> producerConfig,
                         Map<String, Object> consumerConfig,
                         String topic,
                         int partition,
                         TextTransformer transformer,
                         Duration reqTimeout,
                         Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.transformer = transformer;
    this.TRANSACTION_ID = topic + "_" + partition;
    producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);
    this.producer = new KafkaProducer<>(producerConfig);
    this.producer.initTransactions();
    this.consumerConfig = consumerConfig;
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
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

  @Override
  public Mono<Event> last() {
    var lastMono = toMono(KafkaUtils.lastMessage(consumerConfig, topic, partition, timeout));
    return lastMono.map(msg -> transformer.deserialize(msg, Event.class)).map(Try::get).subscribeOn(scheduler);
  }
}
