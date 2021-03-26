package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.jkafka.Tests.producerConf;

class KafkaAdminIT {
  private static final EventStoreAdmin admin = KafkaAdmin.create();

  @Test
  void check() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);

    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    var producer = KafkaUtils.createProducer(producerConf, topic, 0);
    KafkaUtils.sendRecords(producer, topic, 0, List.of("hello p0"), Duration.ofMillis(1000));
    KafkaUtils.sendRecords(producer, topic, 1, List.of("hello p1"), Duration.ofMillis(1000));
    // Then
    StepVerifier.create(admin.exists(topic, 0)).expectNext(true).verifyComplete();
    StepVerifier.create(admin.exists(topic, 1)).expectNext(true).verifyComplete();
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(2).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 0)).expectNext(2L).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 1)).expectNext(2L).verifyComplete();
  }
}