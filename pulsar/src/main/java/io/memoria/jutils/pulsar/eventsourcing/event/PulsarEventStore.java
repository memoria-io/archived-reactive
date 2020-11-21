package io.memoria.jutils.pulsar.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.transformer.StringTransformer;
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

public class PulsarEventStore implements EventStore {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final StringTransformer transformer;

  public PulsarEventStore(String serviceUrl, String adminUrl, StringTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build();
    this.transformer = transformer;
  }

  @Override
  public Flux<Event> add(String topic, Flux<Event> events) {
    return Mono.fromCallable(() -> createProducer(topic))
               .flatMapMany(producer -> events.concatMap(e -> send(producer, e)));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromFuture(admin.topics().getStatsAsync(topic))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  @Override
  public Flux<Event> stream(String topic) {
    return Mono.fromCallable(() -> createConsumer(topic)).flatMapMany(this::receive);
  }

  private Consumer<String> createConsumer(String topic) throws PulsarClientException {
    var consumer = client.newConsumer(Schema.STRING).topic(topic).subscriptionName(topic + "_subscription").subscribe();
    consumer.seek(0);
    return consumer;
  }

  private Producer<String> createProducer(String topic) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(topic).create();
  }

  private Flux<Event> receive(Consumer<String> consumer) {
    return Mono.fromFuture(consumer::receiveAsync)
               .map(Message::getValue)
               .map(value -> transformer.deserialize(value, Event.class).get())
               .repeat();
  }

  private Mono<Event> send(Producer<String> producer, Event event) {
    return Mono.fromCallable(() -> transformer.serialize(event).get())
               .flatMap(msg -> Mono.fromFuture(() -> producer.sendAsync(msg)))
               .map(e -> event);
  }
}
