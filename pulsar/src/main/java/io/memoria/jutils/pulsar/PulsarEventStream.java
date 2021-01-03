package io.memoria.jutils.pulsar;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.EventStream;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.value.Id;
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

public class PulsarEventStream implements EventStream {
  private final PulsarClient client;
  private final PulsarAdmin admin;
  private final StringTransformer transformer;

  public PulsarEventStream(String serviceUrl, String adminUrl, StringTransformer transformer)
          throws PulsarClientException {
    this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    this.admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build();
    this.transformer = transformer;
  }

  @Override
  public <E extends Event> Flux<E> add(Id aggId, Flux<E> events) {
    return Mono.fromCallable(() -> createProducer(aggId.value()))
               .flatMapMany(producer -> events.concatMap(e -> send(producer, e)));
  }

  @Override
  public Mono<Boolean> exists(Id aggId) {
    return Mono.fromFuture(admin.topics().getStatsAsync(aggId.value()))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  @Override
  public <E extends Event> Flux<E> stream(Id aggId, Class<E> as) {
    return Mono.fromCallable(() -> createConsumer(aggId.value())).flatMapMany(i -> this.receive(i, as));
  }

  private Consumer<String> createConsumer(String topic) throws PulsarClientException {
    var consumer = client.newConsumer(Schema.STRING).topic(topic).subscriptionName(topic + "_subscription").subscribe();
    consumer.seek(0);
    return consumer;
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
