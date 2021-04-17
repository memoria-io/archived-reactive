package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.stream.StreamRepo;
import io.vavr.collection.List;
import io.vavr.collection.Map;
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

public class KafkaStream implements StreamRepo {
  public static KafkaStream create(Map<String, Object> producerConfig,
                                   Map<String, Object> consumerConfig,
                                   String topic,
                                   int partition) {
    return create(producerConfig,
                  consumerConfig,
                  topic,
                  partition,
                  Duration.ofMillis(1000),
                  Schedulers.boundedElastic());
  }

  public static KafkaStream create(Map<String, Object> producerConfig,
                                   Map<String, Object> consumerConfig,
                                   String topic,
                                   int partition,
                                   Duration reqTimeout,
                                   Scheduler scheduler) {
    return new KafkaStream(producerConfig, consumerConfig, topic, partition, reqTimeout, scheduler);
  }

  public final String topic;
  public final int partition;
  private final KafkaProducer<String, String> producer;
  private final Map<String, Object> consumerConfig;
  private final Duration timeout;
  private final Scheduler scheduler;

  private KafkaStream(Map<String, Object> producerConfig,
                      Map<String, Object> consumerConfig,
                      String topic,
                      int partition,
                      Duration reqTimeout,
                      Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.producer = createProducer(producerConfig, topic, partition);
    this.consumerConfig = consumerConfig;
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<String> last() {
    var lastMono = toMono(KafkaUtils.lastMessage(consumerConfig, topic, partition, timeout));
    return lastMono.onErrorResume(NoSuchElementException.class, t -> Mono.empty()).subscribeOn(scheduler);
  }

  @Override
  public Mono<Long> publish(List<String> events) {
    return toMono(sendRecords(producer, topic, partition, events, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Flux<String> subscribe(long offset) {
    return Mono.fromCallable(() -> createConsumer(consumerConfig, topic, partition, offset, timeout))
               .flatMapMany(consumer -> Mono.fromCallable(() -> pollOnce(consumer, topic, partition, timeout)).repeat())
               .concatMap(Flux::fromIterable)
               .subscribeOn(scheduler);
  }
}
