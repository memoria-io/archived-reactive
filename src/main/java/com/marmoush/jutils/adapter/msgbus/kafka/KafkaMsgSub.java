package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgSub;
import com.marmoush.jutils.domain.value.msg.SubResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static com.marmoush.jutils.utils.functional.VavrUtils.traversableT;

public class KafkaMsgSub implements MsgSub {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgSub(KafkaConsumer<String, String> consumer, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.consumer = consumer;
  }

  @Override
  public Flux<Try<SubResp>> sub(String topic, String partitionStr, long offset) {
    var partition = Integer.parseInt(partitionStr);
    Consumer<SynchronousSink<List<Try<SubResp>>>> poll = s -> s.next(pollOnce(topic, partition));

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

  private List<Try<SubResp>> pollOnce(String topic, int partition) {
    var t = Try.of(() -> consumer.poll(timeout)).map(toConsumeResponses(topic, partition));
    return List.ofAll(traversableT(t));
  }

  private static Function1<ConsumerRecords<String, String>, List<SubResp>> toConsumeResponses(String topic,
                                                                                              int partition) {
    return crs -> List.ofAll(crs.records(new TopicPartition(topic, partition))).map(KafkaMsgSub::toConsumeResponse);
  }

  private static SubResp toConsumeResponse(ConsumerRecord<String, String> cr) {
    Msg msg = new Msg(cr.key(), cr.value());
    return new SubResp(msg, cr.topic(), cr.partition() + "", Option.of(cr.offset()), Option.of(LocalDateTime.now()));
  }
}
