package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.blockingToMono;
import static io.vavr.API.Some;

public record KafkaMsgReceiver(KafkaConsumer<String, String>consumer, Scheduler scheduler, Duration timeout)
        implements MsgReceiver {

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
                                                                  .map(m -> new Message(Some(m.key()), m.value()));
  }
}
