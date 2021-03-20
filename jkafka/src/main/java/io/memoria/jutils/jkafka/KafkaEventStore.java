package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.memoria.jutils.jcore.vavr.ReactorVavrUtils;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static io.memoria.jutils.jkafka.KafkaUtils.adminClient;
import static io.memoria.jutils.jkafka.KafkaUtils.createKafkaTopic;
import static io.memoria.jutils.jkafka.KafkaUtils.init;
import static io.memoria.jutils.jkafka.KafkaUtils.lastMessage;
import static io.memoria.jutils.jkafka.KafkaUtils.nPartitions;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;
import static io.memoria.jutils.jkafka.KafkaUtils.sendRecord;
import static io.memoria.jutils.jkafka.KafkaUtils.topicExists;

public class KafkaEventStore implements EventStore {
  private final ConcurrentHashMap<String, ReentrantLock> locks;
  private final AdminClient admin;
  private final Duration timeout;
  private final Scheduler scheduler;
  private final TextTransformer transformer;
  private final Map<String, Object> producerConfig;
  private final Map<String, Object> consumerConfig;

  public KafkaEventStore(Map<String, Object> producerConfig,
                         Map<String, Object> consumerConfig,
                         Duration reqTimeout,
                         TextTransformer transformer,
                         Scheduler scheduler) {
    locks = new ConcurrentHashMap<>();
    //    producer = new KafkaProducer<>(producerConfig);
    //    producer.initTransactions();
    //    consumer = new KafkaConsumer<>(consumerConfig);
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.admin = adminClient(producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString());
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
    this.transformer = transformer;
  }

  @Override
  public Mono<Void> createTopic(String topic, int partitions, int replicationFr) {
    return Mono.fromCallable(() -> createKafkaTopic(admin, topic, partitions, (short) replicationFr, timeout))
               .then()
               .subscribeOn(scheduler);
  }

  @Override
  public Mono<Long> currentOffset(String topic, int partition) {
    return Mono.fromCallable(() -> KafkaUtils.currentOffset(admin, topic, partition, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> topicExists(admin, topic)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Event> lastEvent(String topic, int partition) {
    return Mono.fromCallable(() -> lastMessage(admin, new KafkaConsumer<>(consumerConfig), topic, partition, timeout))
               .flatMap(ReactorVavrUtils::toMono)
               .map(msg -> transformer.deserialize(msg, Event.class).get())
               .subscribeOn(scheduler);
  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return Mono.fromCallable(() -> nPartitions(admin, topic, timeout))
               .flatMap(ReactorVavrUtils::toMono)
               .subscribeOn(scheduler);
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, String transactionId, List<Event> events) {
    return Mono.fromRunnable(() -> {
      locks.computeIfAbsent(transactionId, this::lock);
      locks.computeIfPresent(transactionId,(k,v)-> v.)
      producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionId);
      var producer = new KafkaProducer<String, String>(producerConfig);
      producer.initTransactions();
      producer.beginTransaction();
      events.map(transformer::serialize)
            .map(Try::get)
            .map(msg -> Try.of(() -> sendRecord(producer, topic, partition, msg, timeout)))
            .map(Try::get);
      producer.commitTransaction();
    }).then(Mono.just(events)).subscribeOn(scheduler);
  }

  private ReentrantLock lock(String k) {
    var l = new ReentrantLock();
    l.lock();
    return l;
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, long offset) {
    return Mono.fromCallable(() -> init(new KafkaConsumer<>(consumerConfig), topic, partition, offset, timeout))
               .flatMapMany(consumer -> Mono.fromCallable(() -> pollOnce(consumer, topic, partition, timeout)).repeat())
               .concatMap(Flux::fromIterable)
               .map(msg -> transformer.deserialize(msg, Event.class).get())
               .subscribeOn(scheduler);
  }
}