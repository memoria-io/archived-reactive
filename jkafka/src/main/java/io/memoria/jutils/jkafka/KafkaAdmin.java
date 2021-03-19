package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.memoria.jutils.jkafka.KafkaUtils.adminClient;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class KafkaAdmin implements EventStoreAdmin {
  private final AdminClient admin;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaAdmin(Map<String, Object> producerConfig, Duration timeout, Scheduler scheduler) {
    // Setup admin client
    this.admin = adminClient(producerConfig);
    this.timeout = timeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<Integer> createTopic(String topic, int partitions, int replicationFr) {
    return Mono.fromCallable(() -> {
      admin.createTopics(List.of(new NewTopic(topic, partitions, (short) replicationFr)))
           .numPartitions(topic)
           .get(timeout.toMillis(), MILLISECONDS);
      return partitions;
    });
  }

  @Override
  public Mono<Integer> setPartitions(String topic, int partitions) {
    return Mono.fromCallable(() -> {
      setPartitions(topic, partitions);
      return partitions;
    });
  }

  private void setPartitionsNumber(String topic, int partitions)
          throws InterruptedException, ExecutionException, TimeoutException {
    admin.createPartitions(Map.of(topic, NewPartitions.increaseTo(partitions)))
         .all()
         .get(timeout.toMillis(), MILLISECONDS);
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> topicExists(topic)).subscribeOn(scheduler);
  }

  private Boolean topicExists(String topic) throws InterruptedException, ExecutionException {
    ListTopicsResult listTopics = admin.listTopics();
    var names = listTopics.names().get();
    return names.contains(topic);
  }
}
