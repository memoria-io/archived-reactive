package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

public record KafkaSender(KafkaProducer<String, String>kafkaProducer,
                          MessageFilter mf,
                          Scheduler scheduler,
                          Duration timeout) implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.map(msg -> new ProducerRecord<>(mf.topic(), mf.partition(), msg.id().getOrElse(""), msg.value()))
                  .concatMap(this::sendRecord)
                  .map(r -> (r.hasOffset()) ? new Response(r.offset()) : Response.empty());
  }

  private Mono<RecordMetadata> sendRecord(ProducerRecord<String, String> prodRec) {
    return Mono.<RecordMetadata>create(sink -> kafkaProducer.send(prodRec, (metadata, e) -> {
      if (metadata != null)
        sink.success(metadata);
      else
        sink.error(e);
    })).subscribeOn(scheduler);
  }
}
