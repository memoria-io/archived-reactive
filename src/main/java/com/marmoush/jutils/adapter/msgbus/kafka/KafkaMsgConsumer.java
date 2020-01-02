package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

import static com.marmoush.jutils.utils.functional.Functional.*;

public class KafkaMsgConsumer implements MsgConsumer {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgConsumer(Map<String, String> configs, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    Properties properties = new Properties();
    properties.putAll(configs.toJavaMap());
    this.consumer = new KafkaConsumer<>(properties);
  }

  @Override
  public Flux<Try<ConsumeResponse>> consume(String topic, int partition, long offset) {
    consumer.subscribe(List.of(topic).toJavaList());
    // TODO see auto offset
    consumer.seek(new TopicPartition(topic, partition), offset);
    var pollTry = Try.of(() -> consumer.poll(timeout));
    Flux.from(blockingToMono(() -> pollTry, scheduler))
        .flatMap(tryToFluxTry(crs -> toConsumeResponses(crs, topic, partition)));
    return null;
  }

  public Flux<Try<ConsumeResponse>> consumeEach(String topic, int partition) {
    var safePoll = blockingToMono(() -> Try.of(() -> consumer.poll(timeout)), scheduler);
    return Flux.from(safePoll).flatMap(tryToFluxTry(crs -> toConsumeResponses(crs, topic, partition)));
  }

  private static Flux<Try<ConsumeResponse>> toConsumeResponses(ConsumerRecords<String, String> crs,
                                                               String topic,
                                                               int partition) {
    return Flux.fromIterable(crs.records(new TopicPartition(topic, partition)))
               .map(KafkaMsgConsumer::toConsumeResponse)
               .map(Try::success);
  }

  private static Mono<Try<List<ConsumeResponse>>> toConsumeResponseList(ConsumerRecords<String, String> crs,
                                                                        String topic,
                                                                        int partition) {
    return Mono.just(Try.success(List.ofAll(crs.records(new TopicPartition(topic, partition)))
                                     .map(KafkaMsgConsumer::toConsumeResponse)));
  }

  private static ConsumeResponse toConsumeResponse(ConsumerRecord<String, String> cr) {
    Msg msg = new Msg(cr.key(), cr.value());
    return new ConsumeResponse(msg, cr.topic(), cr.partition(), Option.of(cr.offset()), Option.of(LocalDateTime.now()));
  }
}
