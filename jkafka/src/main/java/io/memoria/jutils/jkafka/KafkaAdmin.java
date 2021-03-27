package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.memoria.jutils.jcore.vavr.ReactorVavrUtils;
import org.apache.kafka.clients.admin.AdminClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;
import static io.memoria.jutils.jkafka.KafkaUtils.createAdmin;
import static io.memoria.jutils.jkafka.KafkaUtils.nPartitions;
import static io.memoria.jutils.jkafka.KafkaUtils.topicExists;

public class KafkaAdmin implements EventStoreAdmin {
  public static KafkaAdmin create(String urls) {
    return create(urls, Duration.ofMillis(3000), Schedulers.boundedElastic());
  }

  public static KafkaAdmin create(String url, Duration timeout, Scheduler scheduler) {
    return new KafkaAdmin(url, timeout, scheduler);
  }

  private final AdminClient admin;
  private final Duration timeout;
  private final Scheduler scheduler;

  private KafkaAdmin(String url, Duration timeout, Scheduler scheduler) {
    this.admin = createAdmin(url);
    this.timeout = timeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<Void> createTopic(String topic, int partitions, int replicationFr) {
    var createTopicMono = toMono(KafkaUtils.createTopic(admin, topic, partitions, (short) replicationFr, timeout));
    return createTopicMono.then().subscribeOn(scheduler);
  }

  @Override
  public Mono<Long> currentOffset(String topic, int partition) {
    return toMono(KafkaUtils.currentOffset(admin, topic, partition, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Boolean> exists(String topic, int partition) {
    return toMono(topicExists(admin, topic, partition, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Void> increasePartitionsTo(String topic, int partitions) {
    return toMono(KafkaUtils.increasePartitionsTo(admin, topic, partitions, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return Mono.fromCallable(() -> nPartitions(admin, topic, timeout))
               .flatMap(ReactorVavrUtils::toMono)
               .subscribeOn(scheduler);
  }
}
