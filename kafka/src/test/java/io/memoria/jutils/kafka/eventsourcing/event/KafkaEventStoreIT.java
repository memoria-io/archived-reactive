package io.memoria.jutils.kafka.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.usecase.greet.GreetingTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

class KafkaEventStoreIT {

  @Test
  void run() {
    var eventStore = new KafkaEventStore(KafkaConfigs.producerConf,
                                         KafkaConfigs.consumerConf,
                                         Duration.ofMillis(1000),
                                         Schedulers.boundedElastic(),
                                         new GreetingTransformer());
    new EventStoreTests(eventStore).runAll();
  }
}

