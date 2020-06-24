package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.vavr.control.Option;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.futureToMono;
import static io.vavr.control.Option.none;

public record KafkaMsgSender(KafkaProducer<String, String>kafkaProducer, Scheduler scheduler, Duration timeout)
        implements MsgSender {

  @Override
  public Flux<Option<Message>> send(String topic, int partition, Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler)
                  .map(msg -> new ProducerRecord<>(topic, partition, msg.id().getOrElse(""), msg.message()))
                  .flatMap(this::sendRecord)
                  .map(s -> none());
  }

  private Mono<RecordMetadata> sendRecord(ProducerRecord<String, String> prodRec) {
    return futureToMono(kafkaProducer.send(prodRec), timeout, scheduler);
  }
}
