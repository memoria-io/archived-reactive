package io.memoria.jutils.jpulsar;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException.NotFoundException;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public class PulsarEventStore implements EventStore {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final TextTransformer transformer;

  public PulsarEventStore(String serviceUrl, String adminUrl, TextTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build();
    this.transformer = transformer;
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, List<Event> events) {
    return Mono.fromCallable(() -> client.newProducer(Schema.STRING).topic(topic).create())
               .flatMapMany(producer -> events.map(e -> send(producer, e)));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromFuture(admin.topics().getStatsAsync(topic))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  @Override
  public Mono<Predicate<Event>> endPred(String topic, int partition) {
    return null;
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, int offset) {
    return createConsumer(topic, offset).flatMapMany(i -> receive(i, ));
  }

  private Mono<Consumer<String>> createConsumer(String topic, int offset) {
    return Mono.fromFuture(client.newConsumer(Schema.STRING)
                                 .topic(topic)
                                 .subscriptionName(topic + "_subscription")
                                 .subscribeAsync()).flatMap(c -> Mono.fromFuture(c.seekAsync(offset)).thenReturn(c));
  }

  private Flux<Event> receive(Consumer<String> consumer) {
    return Mono.fromFuture(consumer::receiveAsync)
               .map(Message::getValue)
               .map(value -> transformer.deserialize(value, Event.class).get())
               .repeat();
  }

  private <E extends Event> Mono<E> send(Producer<String> producer, E message) {
    producer.sendAsync()
    return Mono.fromCallable(() -> transformer.serialize(message).get())
               .flatMap(event -> Mono.fromFuture(() -> producer.sendAsync(event)))
               .map(e -> message);
  }
}
