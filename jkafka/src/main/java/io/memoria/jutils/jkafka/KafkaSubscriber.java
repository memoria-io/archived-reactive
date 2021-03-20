package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.msgbus.MsgBusSubscriber;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

import static io.memoria.jutils.jkafka.KafkaUtils.init;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;

public class KafkaSubscriber implements MsgBusSubscriber {
  public final String topic;
  public final int partition;
  public final long offset;

  private final KafkaConsumer<String, String> consumer;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaSubscriber(Map<String, Object> consumerConfig,
                         String topic,
                         int partition,
                         long offset,
                         Duration reqTimeout,
                         Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.offset = offset;
    this.consumer = new KafkaConsumer<>(consumerConfig);
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
  }

  @Override
  public Flux<String> subscribe() {
    return Mono.fromCallable(() -> init(consumer, topic, partition, offset, timeout))
               .flatMapMany(consumer -> Mono.fromCallable(() -> pollOnce(consumer, topic, partition, timeout)).repeat())
               .concatMap(Flux::fromIterable)
               .subscribeOn(scheduler);
  }
}