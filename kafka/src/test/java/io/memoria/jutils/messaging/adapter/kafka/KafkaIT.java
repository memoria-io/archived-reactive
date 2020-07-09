package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.file.DefaultReactiveFileReader;
import io.memoria.jutils.core.utils.file.ReactiveFileReader;
import io.memoria.jutils.core.utils.file.YamlConfigMap;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.Random;

import static io.memoria.jutils.core.utils.file.ReactiveFileReader.resourcePath;
import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.consumer;
import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.producer;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

public class KafkaIT {
  private static final ReactiveFileReader reader = new DefaultReactiveFileReader(Schedulers.boundedElastic());
  private static final YamlConfigMap configs = Objects.requireNonNull(reader.yaml(resourcePath("kafka.yaml").get())
                                                                            .block());
  private static final MessageFilter messageFilter = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 3;

  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public KafkaIT() {
    msgSender = new KafkaSender(producer(configs), messageFilter, Schedulers.boundedElastic(), ofSeconds(1));
    msgReceiver = new KafkaReceiver(consumer(configs), messageFilter, Schedulers.boundedElastic(), ofSeconds(1));
    msgs = Flux.interval(ofMillis(10)).map(i -> new Message("Msg number" + i).withId(i)).take(MSG_COUNT);
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    var publisher = msgSender.apply(msgs);
    var consumer = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    var expectedMsgs = Objects.requireNonNull(msgs.collectList().block()).toArray(new Message[0]);
    StepVerifier.create(consumer).expectNext(expectedMsgs).expectComplete().verify();
  }
}

