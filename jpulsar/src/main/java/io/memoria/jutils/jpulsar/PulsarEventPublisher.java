package io.memoria.jutils.jpulsar;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventPublisher;
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

public class PulsarEventPublisher implements EventPublisher {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final TextTransformer transformer;

  public PulsarEventPublisher(String serviceUrl, String adminUrl, TextTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build();
    this.transformer = transformer;
  }

  @Override
  public Mono<List<Event>> apply(String s, Integer integer, List<Event> events) {
    return null;
  }

  public <E extends Event> Flux<E> publish(Id aggId, Flux<E> events) {
    return Mono.fromCallable(() -> createProducer(aggId.value()))
               .flatMapMany(producer -> events.concatMap(e -> send(producer, e)));
  }

  public Mono<Boolean> exists(Id aggId) {
    return Mono.fromFuture(admin.topics().getStatsAsync(aggId.value()))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  public <E extends Event> Flux<E> subscribe(Id aggId, long offset, Class<E> as) {
    return createConsumer(aggId.value(), offset).flatMapMany(i -> this.receive(i, as));
  }

  private Mono<Consumer<String>> createConsumer(String topic, long offset) {
    return Mono.fromFuture(client.newConsumer(Schema.STRING)
                                 .topic(topic)
                                 .subscriptionName(topic + "_subscription")
                                 .subscribeAsync()).flatMap(c -> Mono.fromFuture(c.seekAsync(offset)).thenReturn(c));
  }

  private Producer<String> createProducer(String topic) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(topic).create();
  }

  private <E extends Event> Flux<E> receive(Consumer<String> consumer, Class<E> as) {
    return Mono.fromFuture(consumer::receiveAsync)
               .map(Message::getValue)
               .map(value -> transformer.deserialize(value, as).get())
               .repeat();
  }

  private <E extends Event> Mono<E> send(Producer<String> producer, E message) {
    return Mono.fromCallable(() -> transformer.serialize(message).get())
               .flatMap(event -> Mono.fromFuture(() -> producer.sendAsync(event)))
               .map(e -> message);
  }
}
