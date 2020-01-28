package com.marmoush.jutils.messaging.adapter.kafka;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.MsgConsumer;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.function.Consumer;

import static com.marmoush.jutils.utils.functional.ReactorVavrUtils.blockingToMono;
import static com.marmoush.jutils.utils.functional.VavrUtils.traverseOfTry;

public class KafkaMsgConsumer implements MsgConsumer {
  private final KafkaConsumer<String, String> consumer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgConsumer(YamlConfigMap map, Scheduler scheduler) {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asMap("reactorKafka").asLong("consumer.request.timeout"));
    this.consumer = new KafkaConsumer<>(map.asMap("kafka").asMap("consumer").toJavaMap());
  }

  @Override
  public Flux<Try<Msg>> consume(String topic, String partitionStr, long offset) {
    var partition = Integer.parseInt(partitionStr);
    var tp = new TopicPartition(topic, partition);
    var subscribeMono = Mono.create(s -> {
      consumer.assign(List.of(tp).toJavaList());
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(tp, offset);
      s.success();
    });
    Consumer<SynchronousSink<List<Try<Msg>>>> poll = s -> s.next(pollOnce(tp));
    var consumerFlux = Flux.generate(poll).flatMap(Flux::fromIterable);
    return Flux.defer(() -> subscribeMono.thenMany(consumerFlux).subscribeOn(scheduler));
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(() -> consumer.close(timeout)), scheduler);
  }

  private List<Try<Msg>> pollOnce(TopicPartition tp) {
    var t = Try.of(() -> consumer.poll(timeout))
               .map(crs -> crs.records(tp))
               .map(List::ofAll)
               .map(l -> l.map(r -> new Msg(r.key(), r.value())));
    return List.ofAll(traverseOfTry(t));
  }
}
