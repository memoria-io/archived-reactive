package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class KafkaMsgProducer implements MsgProducer<RecordMetadata> {
  private final KafkaProducer<String, String> producer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgProducer(KafkaProducer<String, String> producer, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.producer = producer;
  }

  @Override
  public Flux<Try<ProducerResp<RecordMetadata>>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return Try.of(() -> Integer.parseInt(partitionStr))
              .map(partition -> msgFlux.publishOn(scheduler)
                                       .map(msg -> toProducerRecord(msg, topic, partition))
                                       .map(prodRec -> Try.of(() -> producer.send(prodRec)
                                                                            .get(timeout.toMillis(),
                                                                                 TimeUnit.MILLISECONDS)))
                                       .map(t -> t.map(KafkaMsgProducer::toPublishResponse)))
              .getOrElseGet(t -> Flux.just(Try.failure(t)));
  }

  private static ProducerRecord<String, String> toProducerRecord(Msg msg, String topic, int partition) {
    return (msg.pkey.isDefined()) ? new ProducerRecord<>(topic, partition, msg.pkey.get(), msg.value)
                                  : new ProducerRecord<>(topic, msg.value);
  }

  private static ProducerResp<RecordMetadata> toPublishResponse(RecordMetadata meta) {
    return new ProducerResp<>(Option.of(meta));
  }
}
