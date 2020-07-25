package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.messaging.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

public record KafkaSender(KafkaProducer<String, String>kafkaProducer,
                          MessageFilter mf,
                          Scheduler scheduler,
                          Duration timeout) implements MsgSender {

  @Override
  public Mono<Response> apply(Message msg) {
    var key = msg.id().getOrElse(0L) + "";
    var prodRec = new ProducerRecord<>(mf.topic(), mf.partition(), key, msg.value());
    return sendRecord(prodRec).map(r -> (r.hasOffset()) ? new Response(r.offset()) : Response.empty());
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
