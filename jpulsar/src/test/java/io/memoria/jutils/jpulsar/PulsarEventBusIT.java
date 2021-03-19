package io.memoria.jutils.jpulsar;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Random;

class PulsarEventBusIT {

  private final TextTransformer transformer;
  private final String topic;
  private final EventStore store;

  PulsarEventBusIT() throws PulsarClientException {
    transformer = new UserTextTransformer();
    this.topic = "user" + new Random().nextInt(1000);
    this.store = new PulsarEventStore("pulsar://localhost:9001", "http://localhost:9002", transformer);
  }

  @Test
  @DisplayName("Send and receive same events in same order")
  void sendAndReceive() {
    // Given
    var userName = "user_name";
    var msgCount = 1000;
    var events = List.range(0, msgCount).map(i -> (Event) new UserCreated(Id.of(i), userName));
    // When
    var addUsers = store.publish(topic, 0, events);
    var readAddedUsers = store.subscribe(topic, 0, 0).take(msgCount);
    // Then
    StepVerifier.create(addUsers).expectNextCount(msgCount).expectComplete().verify();
    StepVerifier.create(store.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(readAddedUsers)
                .expectNext(new UserCreated(Id.of(0), userName))
                .expectNext(new UserCreated(Id.of(1), userName))
                .expectNextCount(msgCount - 2)
                .expectComplete()
                .verify();
  }
}

