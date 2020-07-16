package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgReceiver;
import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toMessage;

public record NatsReceiver(Connection nc, MessageFilter mf, Scheduler scheduler, Duration timeout)
        implements MsgReceiver {
  private static final Logger log = LoggerFactory.getLogger(NatsReceiver.class.getName());

  @Override
  public Flux<Message> get() {
    var subject = NatsUtils.toSubject(mf.topic(), mf.partition());
    Flux<Message> f = Flux.create(s -> {
      var dispatcher = nc.createDispatcher($ -> {});
      log.info("subscribing to: " + subject);
      var sub = dispatcher.subscribe(subject, m -> s.next(toMessage(m)));

      s.onDispose(() -> {
        log.info("Dispose signal, Unsubscribing now from subject: " + sub.getSubject());
        dispatcher.unsubscribe(sub);
      });
      s.onCancel(() -> log.info("Cancellation signal to subject:" + sub.getSubject()));
    });
    return Flux.defer(() -> f.subscribeOn(scheduler).skip(mf.offset()).timeout(timeout));
  }
}
