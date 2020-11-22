package io.memoria.jutils.kafka.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventHandlerTests;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.SocialNetworkTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

class KafkaEventHandlerIT {

  @Test
  void run() {
    var eventStore = new KafkaEventStore(KafkaConfigs.producerConf,
                                         KafkaConfigs.consumerConf,
                                         Duration.ofMillis(1000),
                                         Schedulers.boundedElastic(),
                                         new SocialNetworkTransformer());
    new EventHandlerTests(eventStore).runAll();
  }
}

