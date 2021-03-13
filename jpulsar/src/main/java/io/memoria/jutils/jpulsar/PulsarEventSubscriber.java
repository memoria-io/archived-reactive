package io.memoria.jutils.jpulsar;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventSubscriber;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
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

public class PulsarEventSubscriber implements EventSubscriber {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final TextTransformer transformer;

  public PulsarEventSubscriber(String serviceUrl, String adminUrl, TextTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build();
    this.transformer = transformer;
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return null;
  }

  @Override
  public Mono<Predicate<Event>> lastEventPredicate(String topic, int partition) {
    return null;
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, long startOffset) {
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
