package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.function.Consumer;

import static com.marmoush.jutils.utils.functional.VavrUtils.traversableT;

public class KafkaMsgConsumer implements MsgConsumer<Void> {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgConsumer(KafkaConsumer<String, String> consumer, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.consumer = consumer;
  }

  @Override
  public Flux<Try<ConsumerResp<Void>>> consume(String topic, String partitionStr, long offset) {
    var partition = Integer.parseInt(partitionStr);
    Consumer<SynchronousSink<List<Try<ConsumerResp<Void>>>>> poll = s -> s.next(pollOnce(topic, partition));

    var subscribeMono = Mono.create(s -> {
      consumer.assign(List.of(new TopicPartition(topic, partition)).toJavaList());
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(new TopicPartition(topic, partition), offset);
      s.success();
    });
    var consumerFlux = Flux.generate(poll).flatMap(Flux::fromIterable);
    return Flux.defer(() -> subscribeMono.thenMany(consumerFlux).subscribeOn(scheduler));
  }

  private List<Try<ConsumerResp<Void>>> pollOnce(String topic, int partition) {
    var t = Try.of(() -> consumer.poll(timeout)).map(toConsumeResponses(topic, partition));
    return List.ofAll(traversableT(t));
  }

  private static Function1<ConsumerRecords<String, String>, List<ConsumerResp<Void>>> toConsumeResponses(String topic,
                                                                                                         int partition) {
    return crs -> List.ofAll(crs.records(new TopicPartition(topic, partition)))
                      .map(cr -> new ConsumerResp<Void>(new Msg(cr.key(), cr.value())));
  }
}
