package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgPub;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PubResp;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class KafkaMsgPub implements MsgPub {
  private final KafkaProducer<String, String> producer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgPub(KafkaProducer<String, String> producer, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.producer = producer;
  }

  @Override
  public Flux<Try<PubResp>> pub(Flux<Msg> msgFlux, String topic, String partitionStr) {
    Try<Integer> partitionTry = Try.of(() -> Integer.parseInt(partitionStr));
    if (partitionTry.isSuccess()) {
      return msgFlux.publishOn(scheduler)
                    .map(msg -> new ProducerRecord<>(topic, partitionTry.get(), msg.key, msg.value))
                    .map(prodRec -> Try.of(() -> producer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS)))
                    .map(t -> t.map(KafkaMsgPub::toPublishResponse));

    } else {
      return Flux.just(Try.failure(partitionTry.getCause()));
    }
  }

  private static PubResp toPublishResponse(RecordMetadata meta) {
    return new PubResp(meta.topic(),
                               meta.partition() + "",
                       Option.of(meta.offset()),
                       Option.of(meta.timestamp())
                                     .map(Instant::ofEpochMilli)
                                     .map(t -> LocalDateTime.ofInstant(t, ZoneOffset.UTC)));
  }
}
