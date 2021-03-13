//package io.memoria.jutils.jpulsar;
//
//import io.memoria.jutils.jcore.eventsourcing.Event;
//import io.memoria.jutils.jcore.eventsourcing.EventPublisher;
//import io.memoria.jutils.jcore.eventsourcing.EventSubscriber;
//import io.memoria.jutils.jcore.id.Id;
//import io.memoria.jutils.jcore.text.TextTransformer;
//import io.vavr.control.Try;
//import org.apache.pulsar.client.api.PulsarClientException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//
//import java.util.Random;
//
//class PulsarEventBusIT {
//
//  private final TextTransformer json;
//  private final Id aggId;
//  private final EventPublisher publisher;
//  private final EventSubscriber subscriber;
//
//  PulsarEventBusIT() throws PulsarClientException {
//    this.json = new TextTransformer() {
//      @Override
//      @SuppressWarnings("unchecked")
//      public <T> Try<T> deserialize(String str, Class<T> tClass) {
//        var user = str.split(":");
//        return Try.success((T) new UserCreated(Id.of(user[0]), user[1]));
//      }
//
//      @Override
//      public <T> Try<String> serialize(T t) {
//        var user = (UserCreated) t;
//        return Try.success("%s:%s".formatted(user.eventId.value(), user.name));
//      }
//    };
//    this.aggId = Id.of("user" + new Random().nextInt(1000));
//    this.publisher = new PulsarEventPublisher("pulsar://localhost:9001", "http://localhost:9002", json);
//  }
//
//  @Test
//  @DisplayName("Send and receive same events in same order")
//  void sendAndReceive() {
//    // Given
//    var userName = "user_name";
//    var msgCount = 1000;
//    var events = Flux.range(0, msgCount).map(i -> (Event) new UserCreated(Id.of(i), userName));
//    // When
//    var addUsers = publisher.publish(aggId, events);
//    var readAddedUsers = publisher.subscribe(aggId, 0, UserCreated.class).take(msgCount);
//    // Then
//    StepVerifier.create(addUsers).expectNextCount(msgCount).expectComplete().verify();
//    StepVerifier.create(publisher.exists(aggId)).expectNext(true).expectComplete().verify();
//    StepVerifier.create(readAddedUsers)
//                .expectNext(new UserCreated(Id.of(0), userName))
//                .expectNext(new UserCreated(Id.of(1), userName))
//                .expectNextCount(msgCount - 2)
//                .expectComplete()
//                .verify();
//  }
//}
//
