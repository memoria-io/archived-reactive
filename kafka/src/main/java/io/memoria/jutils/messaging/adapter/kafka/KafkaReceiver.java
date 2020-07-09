package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

public record KafkaReceiver(KafkaConsumer<String, String>consumer,
                            MessageFilter mf,
                            Scheduler scheduler,
                            Duration timeout) implements MsgReceiver {

  @Override
  public Flux<Message> get() {
    var tp = new TopicPartition(mf.topic(), mf.partition());
    consumer.assign(List.of(tp).toJavaList());
    // must call poll before seek
    consumer.poll(timeout);
    consumer.seek(tp, mf.offset());
    return Flux.<Flux<Message>>generate(c -> c.next(pollOnce(tp))).flatMap(f -> f).subscribeOn(scheduler);
  }

  private Flux<Message> pollOnce(TopicPartition tp) {
    return Mono.fromCallable(() -> consumer.poll(timeout))
               .map(crs -> crs.records(tp))
               .flatMapMany(Flux::fromIterable)
               .map(m -> new Message(m.value()).withId(m.key()));
  }
}
