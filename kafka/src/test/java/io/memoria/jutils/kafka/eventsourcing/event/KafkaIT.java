package io.memoria.jutils.kafka.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.event.GreetingTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

class KafkaIT {
  private final EventStoreTests eventStoreTests;

  KafkaIT() {
    var kafkaEventStore = new KafkaEventStore(KafkaConfigs.producerConf,
                                              KafkaConfigs.consumerConf,
                                              Duration.ofMillis(1000),
                                              Schedulers.boundedElastic(),
                                              new GreetingTransformer());
    this.eventStoreTests = new EventStoreTests(kafkaEventStore);
  }

  @Test
  void run() {
    eventStoreTests.runAll();
  }
}

