package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Objects;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.blockingToMono;

public class KafkaMsgReceiver implements MsgReceiver {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgReceiver(KafkaConsumer<String, String> consumer, Scheduler scheduler, Duration timeout) {
    this.consumer = consumer;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    KafkaMsgReceiver that = (KafkaMsgReceiver) o;
    return consumer.equals(that.consumer) && scheduler.equals(that.scheduler) && timeout.equals(that.timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consumer, scheduler, timeout);
  }

  @Override
  public Flux<Message> receive(String topic, int partition, long offset) {
    var tp = new TopicPartition(topic, partition);
    consumer.assign(List.of(tp).toJavaList());
    // must call poll before seek
    consumer.poll(timeout);
    consumer.seek(tp, offset);
    return Flux.<Flux<Message>>generate(c -> c.next(pollOnce(tp))).flatMap(f -> f);
  }

  private Flux<Message> pollOnce(TopicPartition tp) {
    return blockingToMono(() -> consumer.poll(timeout), scheduler).map(crs -> crs.records(tp))
                                                                  .flatMapMany(Flux::fromIterable)
                                                                  .map(m -> new Message(m.value()).withId(m.key()));
  }

}
