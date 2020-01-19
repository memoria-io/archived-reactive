package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.entity.Msg;
import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.marmoush.jutils.utils.functional.ReactorVavrUtils.blockingToMono;

public class KafkaMsgProducer implements MsgProducer<RecordMetadata> {
  private final KafkaProducer<String, String> kafkaProducer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgProducer(YamlConfigMap map, Scheduler scheduler) {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asMap("reactorKafka").asLong("producer.request.timeout"));
    this.kafkaProducer = new KafkaProducer<>(map.asMap("kafka").asMap("producer").toJavaMap());
  }

  @Override
  public Flux<Try<Void>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return Try.of(() -> Integer.parseInt(partitionStr))
              .map(partition -> msgFlux.publishOn(scheduler)
                                       .map(msg -> new ProducerRecord<>(topic, partition, msg.id, msg.value))
                                       .map(prodRec -> Try.run(() -> send(prodRec))))
              .getOrElseGet(t -> Flux.just(Try.failure(t)));
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(() -> kafkaProducer.close(timeout)), scheduler);
  }

  private RecordMetadata send(ProducerRecord<String, String> prodRec)
          throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
    return kafkaProducer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
  }
}
