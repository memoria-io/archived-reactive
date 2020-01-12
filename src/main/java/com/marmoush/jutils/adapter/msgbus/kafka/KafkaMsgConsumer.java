package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Properties;
import java.util.function.Consumer;

import static com.marmoush.jutils.utils.functional.VavrUtils.traversableT;

public class KafkaMsgConsumer implements MsgConsumer {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgConsumer(Map<String, Object> configs, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    Properties properties = new Properties();
    properties.putAll(configs.toJavaMap());
    this.consumer = new KafkaConsumer<>(properties);
  }

  @Override
  public Flux<Try<ConsumeResponse>> consume(String topic, int partition, long offset) {
    Consumer<SynchronousSink<List<Try<ConsumeResponse>>>> poll = s -> s.next(pollOnce(topic, partition));

    var subscribeMono = Mono.create(s -> {
      consumer.assign(List.of(new TopicPartition(topic, partition)).toJavaList());
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(new TopicPartition(topic, partition), offset);
      s.success();
    });
    var flux = Flux.generate(poll).flatMap(Flux::fromIterable);
    return Flux.defer(() -> subscribeMono.thenMany(flux).subscribeOn(scheduler));
  }

  private List<Try<ConsumeResponse>> pollOnce(String topic, int partition) {
    var t = Try.of(() -> consumer.poll(timeout)).map(crs -> toConsumeResponses(crs, topic, partition));
    return List.ofAll(traversableT(t));
  }

  private static List<ConsumeResponse> toConsumeResponses(ConsumerRecords<String, String> crs,
                                                          String topic,
                                                          int partition) {
    var prt = new TopicPartition(topic, partition);
    return List.ofAll(crs.records(prt)).map(KafkaMsgConsumer::toConsumeResponse);
  }

  private static ConsumeResponse toConsumeResponse(ConsumerRecord<String, String> cr) {
    Msg msg = new Msg(cr.key(), cr.value());
    return new ConsumeResponse(msg, cr.topic(), cr.partition(), Option.of(cr.offset()), Option.of(LocalDateTime.now()));
  }
}
