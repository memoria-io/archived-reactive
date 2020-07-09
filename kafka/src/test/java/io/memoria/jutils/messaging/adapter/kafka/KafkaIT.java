package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.file.DefaultReactiveFileReader;
import io.memoria.jutils.core.utils.file.ReactiveFileReader;
import io.memoria.jutils.core.utils.file.YamlConfigMap;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static io.memoria.jutils.core.utils.file.ReactiveFileReader.resourcePath;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static reactor.core.scheduler.Schedulers.elastic;

public class KafkaIT {
  private static final ReactiveFileReader reader = new DefaultReactiveFileReader(Schedulers.boundedElastic());
  private static final YamlConfigMap kafkaConfigs = Objects.requireNonNull(reader.yaml(resourcePath("kafka.yaml").get())
                                                                                 .block());

  private static final Map<String, Object> producerConfigMap = kafkaConfigs.asYamlConfigMap("producer")
                                                                           .get()
                                                                           .map()
                                                                           .toJavaMap();
  private static final KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerConfigMap);
  private static final Map<String, Object> consumerConfigMap = kafkaConfigs.asYamlConfigMap("consumer")
                                                                           .get()
                                                                           .map()
                                                                           .toJavaMap();
  private static final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(consumerConfigMap);

  private static final MessageFilter messageFilter = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public KafkaIT() {
    msgSender = new KafkaMsgSender(kafkaProducer, messageFilter, elastic(), ofSeconds(1));
    // TODO check why Schedulers.elastic() causes multithreading error with Kafka
    msgReceiver = new KafkaMsgReceiver(kafkaConsumer, messageFilter, Schedulers.immediate(), ofSeconds(1));
    msgs = Flux.interval(ofMillis(10)).map(i -> new Message("Msg number" + i).withId(i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    var publisher = msgSender.apply(msgs.take(MSG_COUNT));
    var consumer = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(consumer).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}
