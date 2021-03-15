package io.memoria.jutils.jpulsar;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException.NotFoundException;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.jpulsar.PulsarUtils.createConsumer;
import static io.memoria.jutils.jpulsar.PulsarUtils.createProducer;
import static io.memoria.jutils.jpulsar.PulsarUtils.createTransaction;
import static io.memoria.jutils.jpulsar.PulsarUtils.send;

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
  public Mono<Boolean> exists(String topic) {
    return Mono.fromFuture(admin.topics().getStatsAsync(topic))
               .map(stats -> true)
               .onErrorReturn(NotFoundException.class, false);
  }

  @Override
  public Mono<Event> lastEvent(String topic, int partition) {
    return null;
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, List<Event> events) {
    var msgsFlux = serialize(events);
    var trans = createTransaction(client);
    var prod = createProducer(client, topic, partition);
    return trans.flatMapMany(tx -> prod.flatMapMany(p -> send(p, tx, msgsFlux))).then(Mono.just(events));
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, int offset) {
    return createConsumer(client, topic, offset).flatMapMany(PulsarUtils::receive)
                                                .map(s -> transformer.deserialize(s, Event.class).get());
  }

  private Flux<String> serialize(List<Event> events) {
    return Flux.fromIterable(events).map(transformer::serialize).map(Try::get);
  }
}
