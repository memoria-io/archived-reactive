package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.msgbus.MsgBusAdmin;
import io.memoria.jutils.jcore.vavr.ReactorVavrUtils;
import org.apache.kafka.clients.admin.AdminClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static io.memoria.jutils.jkafka.KafkaUtils.adminClient;
import static io.memoria.jutils.jkafka.KafkaUtils.createKafkaTopic;
import static io.memoria.jutils.jkafka.KafkaUtils.nPartitions;
import static io.memoria.jutils.jkafka.KafkaUtils.topicExists;

public class KafkaAdmin implements MsgBusAdmin {
  private final AdminClient admin;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaAdmin(String url, Duration timeout, Scheduler scheduler) {
    this.admin = adminClient(url);
    this.timeout = timeout;
    this.scheduler = scheduler;
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

  //  @Override
  //  public Mono<Event> lastEvent(String topic, int partition) {
  //    return Mono.fromCallable(() -> lastMessage(admin,consumer, topic, partition, timeout))
  //               .flatMap(ReactorVavrUtils::toMono)
  //               .map(msg -> transformer.deserialize(msg, Event.class).get())
  //               .subscribeOn(scheduler);
  //  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return Mono.fromCallable(() -> nPartitions(admin, topic, timeout))
               .flatMap(ReactorVavrUtils::toMono)
               .subscribeOn(scheduler);
  }
}
