package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public record NatsEventStore(Connection nc, StringTransformer transformer, Duration timeout, Scheduler scheduler)
        implements EventStore {
  private static final Logger log = LoggerFactory.getLogger(NatsEventStore.class.getName());

  @Override
  public Flux<String> add(String topic, Flux<Event> events) {
    return events.map(e -> publish(topic, e)).subscribeOn(scheduler).timeout(timeout);
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return stream(topic).take(1)
                        .timeout(Duration.ofMillis(100))
                        .count()
                        .map(count -> true)
                        .subscribeOn(scheduler)
                        .onErrorReturn(false);
  }

  @Override
  public Flux<Event> stream(String topic) {
    Flux<Event> f = Flux.create(s -> {
      var dispatcher = nc.createDispatcher($ -> {});
      log.info("subscribing to: " + topic);
      var sub = dispatcher.subscribe(topic,
                                     m -> s.next(transformer.deserialize(new String(m.getData()), Event.class).get()));
      s.onDispose(() -> {
        log.info("Dispose signal, Unsubscribing now from subject: " + sub.getSubject());
        dispatcher.unsubscribe(sub);
      });
      s.onCancel(() -> log.info("Cancellation signal to subject:" + sub.getSubject()));
    });
    return Flux.defer(() -> f.subscribeOn(scheduler).timeout(timeout));
  }

  private String publish(String topic, Event e) {
    var msg = this.transformer.serialize(e).get().getBytes(StandardCharsets.UTF_8);
    nc.publish(topic, msg);
    return e.id().value();
  }
}
