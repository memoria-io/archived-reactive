package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.vavr.collection.List;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.memoria.jutils.jkafka.TestConfigs.producerConf;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

class KafkaAdminIT {
  private static final EventStoreAdmin admin;

  static {
    var url = producerConf.get(BOOTSTRAP_SERVERS_CONFIG).toString();
    admin = new KafkaAdmin(url, Duration.ofMillis(1000), Schedulers.boundedElastic());
  }

  @Test
  void check() throws InterruptedException, ExecutionException, TimeoutException {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);

    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    KafkaUtils.sendRecords(new KafkaProducer<>(producerConf), topic, 0, List.of("hello p0"), Duration.ofMillis(1000));
    KafkaUtils.sendRecords(new KafkaProducer<>(producerConf), topic, 1, List.of("hello p1"), Duration.ofMillis(1000));
    // Then
    StepVerifier.create(admin.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(2).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 0)).expectNext(2L).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 1)).expectNext(2L).verifyComplete();
  }
}