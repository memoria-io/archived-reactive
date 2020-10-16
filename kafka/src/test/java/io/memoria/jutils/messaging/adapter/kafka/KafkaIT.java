package io.memoria.jutils.messaging.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.memoria.jutils.adapter.transformer.yaml.YamlJackson;
import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgReceiver;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.vavr.jackson.datatype.VavrModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.Random;

import static java.time.Duration.ofMillis;

class KafkaIT {
  private static final int MSG_COUNT = 10;
  private static final MessageFilter messageFilter = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final MsgSender msgSender;
  private static final MsgReceiver msgReceiver;

  static {
    // File
    var files = new FileUtils(Schedulers.boundedElastic());
    ObjectMapper jacksonMapper = new ObjectMapper(new YAMLFactory());
    jacksonMapper.registerModule(new VavrModule());
    var yaml = new YamlJackson(jacksonMapper);
    //
    var kafkaConfig = yaml.deserialize(files.readResource("kafka.yaml").block(), KafkaConfig.class).get();
    // Kafka
    msgSender = kafkaConfig.createSender(messageFilter, Schedulers.boundedElastic());
    msgReceiver = kafkaConfig.createReceiver(messageFilter, Schedulers.boundedElastic());
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones, and In same order")
  void kafkaPubSub() {
    // Given
    var infiniteMsgsFlux = Flux.interval(ofMillis(100)).map(KafkaIT::iToMessage);
    var limitedMsgsFlux = infiniteMsgsFlux.take(MSG_COUNT);
    var limitedMsgsArr = Objects.requireNonNull(Flux.interval(ofMillis(100))
                                                    .map(KafkaIT::iToMessage)
                                                    .take(MSG_COUNT)
                                                    .collectList()
                                                    .block()).toArray(Message[]::new);
    var publisher = msgSender.apply(limitedMsgsFlux);
    var consumer = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(consumer).expectNext(limitedMsgsArr).expectComplete().verify();
  }

  private static Message iToMessage(long i) {
    return new Message("Msg number" + i).withId(i);
  }
}

