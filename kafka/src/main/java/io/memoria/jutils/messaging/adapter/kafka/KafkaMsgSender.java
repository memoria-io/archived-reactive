package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Objects;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.futureToMono;

public class KafkaMsgSender implements MsgSender {
  private final KafkaProducer<String, String> kafkaProducer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgSender(KafkaProducer<String, String> kafkaProducer, Scheduler scheduler, Duration timeout) {
    this.kafkaProducer = kafkaProducer;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    KafkaMsgSender that = (KafkaMsgSender) o;
    return kafkaProducer.equals(that.kafkaProducer) && scheduler.equals(that.scheduler) && timeout.equals(that.timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kafkaProducer, scheduler, timeout);
  }

  @Override
  public Flux<Response> send(String topic, int partition, Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler)
                  .map(msg -> new ProducerRecord<>(topic, partition, msg.id().getOrElse(""), msg.value()))
                  .flatMap(this::sendRecord)
                  .map(s -> Response.empty());
  }

  private Mono<RecordMetadata> sendRecord(ProducerRecord<String, String> prodRec) {
    return futureToMono(kafkaProducer.send(prodRec), timeout, scheduler);
  }
}
