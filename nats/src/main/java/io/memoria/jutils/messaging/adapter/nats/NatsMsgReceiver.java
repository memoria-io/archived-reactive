package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Objects;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toMessage;

public class NatsMsgReceiver implements MsgReceiver {
  private static final Logger log = LoggerFactory.getLogger(NatsMsgReceiver.class.getName());
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgReceiver(Connection nc, Scheduler scheduler, Duration timeout) {
    this.nc = nc;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    NatsMsgReceiver that = (NatsMsgReceiver) o;
    return nc.equals(that.nc) && scheduler.equals(that.scheduler) && timeout.equals(that.timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nc, scheduler, timeout);
  }

  @Override
  public Flux<Message> receive(String topic, int partition, long offset) {
    var subject = NatsUtils.toSubject(topic, partition);
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
    return Flux.defer(() -> f.subscribeOn(scheduler).skip(offset).timeout(timeout));
  }
}
