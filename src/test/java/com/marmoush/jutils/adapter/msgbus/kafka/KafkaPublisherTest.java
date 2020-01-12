package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import dummy.Constants;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class KafkaPublisherTest {
  private static Map<String, String> config = HashMap.of("bootstrap.servers",
                                                         Constants.IP + ":" + Constants.KAFKA_PORT,
                                                         "acks",
                                                         "all",
                                                         "retries",
                                                         "0",
                                                         "batch.size",
                                                         "1",
                                                         "linger.ms",
                                                         "1",
                                                         "key.serializer",
                                                         "org.apache.kafka.common.serialization.StringSerializer",
                                                         "value.serializer",
                                                         "org.apache.kafka.common.serialization.StringSerializer");

  public static void main(String[] args) throws InterruptedException {
    MsgPublisher msgPublisher = new KafkaMsgPublisher(config, Schedulers.elastic(), Duration.ofSeconds(1));
    var msgs = Flux.interval(Duration.ofMillis(100)).map(i -> new Msg(i + "", "Msg number" + i));
    Flux<Try<PublishResponse>> publish = msgPublisher.publish(Constants.KAFKA_TOPIC, 0, msgs).
            doOnNext(i -> {
              if (i.isSuccess()) {
                System.out.println(i.get().topic + ":" + i.get().partition + ":" + i.get().offset.get());
              } else {
                System.out.println(i.getCause().getMessage());
              }
            });
    publish.subscribe();
    Thread.sleep(60000);
  }
}
