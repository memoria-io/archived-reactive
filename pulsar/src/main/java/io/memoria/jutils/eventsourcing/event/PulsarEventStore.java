package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.vavr.control.Try;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.PulsarClientException.NotFoundException;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class PulsarEventStore implements EventStore {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final Duration frequency;
  private final StringTransformer transformer;

  public PulsarEventStore(String serviceUrl, Duration frequency, StringTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(serviceUrl).build();
    this.frequency = frequency;
    this.transformer = transformer;
  }

  @Override
  public Flux<String> add(String topic, Flux<Event> events) {
    return Mono.fromCallable(() -> createProducer(topic)).flatMapMany(producer -> sendEvents(producer, events));
  }

  private Flux<String> sendEvents(Producer<String> producer, Flux<Event> events) {
    return events.map(transformer::serialize)
                 .map(Try::get)
                 .flatMap(msg -> Mono.fromFuture(producer.sendAsync(msg)).map(Object::toString));
  }

  private Producer<String> createProducer(String topic) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(topic).create();
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromFuture(admin.topics().getStatsAsync(topic))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  @Override
  public Flux<Event> stream(String topic) {
    return Mono.fromCallable(() -> createConsumer(topic))
               .flatMapMany(consumer -> Flux.interval(frequency).concatMap(i -> receive(consumer)));
  }

  private Consumer<String> createConsumer(String topic) throws PulsarClientException {
    var consumer = client.newConsumer(Schema.STRING).topic(topic).subscriptionName(topic + "_subscription").subscribe();
    consumer.seek(0);
    return consumer;
  }

  private Mono<Event> receive(Consumer<String> consumer) {
    return Mono.fromFuture(consumer.receiveAsync())
               .map(Message::getValue)
               .map(value -> transformer.deserialize(value, Event.class).get());
  }

  private Mono<Void> send(Producer<String> producer, String payload) {
    return Mono.fromFuture(producer.sendAsync(payload)).then();
  }
}
