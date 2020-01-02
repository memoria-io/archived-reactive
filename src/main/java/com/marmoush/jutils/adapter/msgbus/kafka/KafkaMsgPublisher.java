package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.marmoush.jutils.utils.functional.Functional.blockingToMono;

public class KafkaMsgPublisher implements MsgPublisher {
  private final KafkaProducer<String, String> producer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgPublisher(Map<String, String> configs, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    Properties properties = new Properties();
    properties.putAll(configs.toJavaMap());
    this.producer = new KafkaProducer<>(properties);
  }

  @Override
  public Mono<Try<PublishResponse>> publish(String topic, int partition, Msg msg) {
    var record = new ProducerRecord<>(topic, partition, msg.key, msg.value);
    var recordTry = Try.of(() -> producer.send(record).get(timeout.toMillis(), TimeUnit.MILLISECONDS))
                       .map(KafkaMsgPublisher::toPublishResponse);
    return blockingToMono(() -> recordTry, scheduler);
  }

  private static PublishResponse toPublishResponse(RecordMetadata meta) {
    return new PublishResponse(meta.topic(),
                               meta.partition(),
                               Option.of(meta.offset()),
                               Option.of(meta.timestamp())
                                     .map(Instant::ofEpochMilli)
                                     .map(t -> LocalDateTime.ofInstant(t, ZoneOffset.UTC)));
  }
}
