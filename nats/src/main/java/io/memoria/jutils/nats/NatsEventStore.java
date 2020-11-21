package io.memoria.jutils.nats;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.nats.client.Connection;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import io.nats.streaming.SubscriptionOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class NatsEventStore implements EventStore {

  private static final Logger log = LoggerFactory.getLogger(NatsEventStore.class.getName());
  private final StreamingConnection sc;
  private final Duration timeout;
  private final Scheduler scheduler;
  private final StringTransformer transformer;

  public NatsEventStore(Connection nc, Duration timeout, Scheduler scheduler, StringTransformer transformer)
          throws IOException, InterruptedException {
    var ob = new Options.Builder().natsConn(nc).clientId("jutils").clusterId("test-cluster");
    var cf = new StreamingConnectionFactory(ob.build());
    this.sc = cf.createConnection();
    this.timeout = timeout;
    this.scheduler = scheduler;
    this.transformer = transformer;
  }

  @Override
  public Flux<Event> add(String topic, Flux<Event> events) {
    return events.concatMap(e -> publish(topic, e)).subscribeOn(scheduler).timeout(timeout);
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return stream(topic).take(1)
                        .timeout(timeout)
                        .count()
                        .map(count -> true)
                        .subscribeOn(scheduler)
                        .onErrorReturn(false);
  }

  @Override
  public Flux<Event> stream(String topic) {
    Flux<Event> f = Flux.create(s -> {
      try {
        sc.subscribe(topic, m -> {
          s.next(transformer.deserialize(new String(m.getData()), Event.class).get());
        }, new SubscriptionOptions.Builder().deliverAllAvailable().build());
      } catch (IOException | InterruptedException | TimeoutException e) {
        e.printStackTrace();
        s.error(e);
        Thread.currentThread().interrupt();
      }
      log.info("subscribing to: " + topic);
      s.onDispose(() -> log.info("Dispose signal, Unsubscribing now from subject: " + topic));
      s.onCancel(() -> log.info("Cancellation signal to subject:" + topic));
    });
    return Flux.defer(() -> f.subscribeOn(scheduler));
  }

  private Mono<Event> publish(String topic, Event event) {
    return Mono.fromCallable(() -> {
      var msg = this.transformer.serialize(event).get().getBytes(StandardCharsets.UTF_8);
      sc.publish(topic, msg);
      return event;
    }).subscribeOn(scheduler);
  }
}
