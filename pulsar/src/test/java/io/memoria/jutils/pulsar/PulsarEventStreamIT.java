package io.memoria.jutils.pulsar;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.EventStream;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.jackson.transformer.JacksonUtils;
import io.memoria.jutils.jackson.transformer.json.JsonJackson;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Random;

class PulsarEventStreamIT {
  private static record UserCreated(Id eventId, String name, int age) implements Event {
    @Override
    public Id aggId() {
      return new Id(name);
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }

  private final StringTransformer json;
  private final Id aggId;
  private final EventStream eventStream;

  PulsarEventStreamIT() throws PulsarClientException {
    this.json = new JsonJackson(JacksonUtils.defaultJson());
    this.aggId = new Id("user" + new Random().nextInt(1000));
    this.eventStream = new PulsarEventStream("pulsar://localhost:9001", "http://localhost:9002", json);
  }

  @Test
  @DisplayName("Send and receive same events in same order")
  void sendAndReceive() {
    // Given
    var userName = "user_name";
    var userAge = 20;
    var msgCount = 1000;
    var events = Flux.range(0, msgCount).map(i -> (Event) new UserCreated(Id.of(i), userName, userAge));
    // When
    var addUsers = eventStream.add(aggId, events);
    var readAddedUsers = eventStream.stream(aggId, UserCreated.class).take(msgCount);
    // Then
    StepVerifier.create(addUsers).expectNextCount(msgCount).expectComplete().verify();
    StepVerifier.create(eventStream.exists(aggId)).expectNext(true).expectComplete().verify();
    StepVerifier.create(readAddedUsers)
                .expectNext(new UserCreated(Id.of(0), userName, userAge))
                .expectNext(new UserCreated(Id.of(1), userName, userAge))
                .expectNextCount(msgCount - 2)
                .expectComplete()
                .verify();
  }
}

