package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.msgbus.MsgBusPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

import static io.memoria.jutils.jkafka.KafkaUtils.sendRecord;

public class KafkaPublisher implements MsgBusPublisher {
  public final String topic;
  public final int partition;
  public final String TRANSACTION_ID;
  
  private final KafkaProducer<String, String> producer;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaPublisher(Map<String, Object> producerConfig,
                        String topic,
                        int partition,
                        Duration reqTimeout,
                        Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.TRANSACTION_ID = topic + "_" + partition;
    producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);
    this.producer = new KafkaProducer<>(producerConfig);
    this.producer.initTransactions();
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<Void> beginTransaction() {
    return Mono.<Void>fromRunnable(producer::beginTransaction).subscribeOn(scheduler);
  }

  @Override
  public Mono<Void> abortTransaction() {
    return Mono.<Void>fromRunnable(producer::abortTransaction).subscribeOn(scheduler);
  }

  @Override
  public Mono<Void> commitTransaction() {
    return Mono.<Void>fromRunnable(producer::commitTransaction).subscribeOn(scheduler);
  }

  @Override
  public Mono<Long> publish(String msg) {
    return Mono.fromCallable(() -> sendRecord(producer, topic, partition, msg, timeout)).subscribeOn(scheduler);
  }
}