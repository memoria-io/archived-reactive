package io.memoria.jutils.jkafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class KafkaUtils {
  public static AdminClient adminClient(Map<String, Object> producerConfig) {
    var serverURL = producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
    var config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverURL);
    return AdminClient.create(config);
  }

  public static Mono<String> last(KafkaConsumer<String, String> consumer,
                                 String topic,
                                 int partition,
                                 Duration timeout) {
    return Mono.fromCallable(() -> {
      var tp = new TopicPartition(topic, partition);
      consumer.assign(List.of(tp));
      consumer.seekToEnd(List.of(tp));
      return consumer.position(tp);
    }).flatMap(last -> {
      init(consumer, topic, partition, last - 1, timeout);
      return pollOnce(consumer, topic, partition, timeout).last();
    });
  }

  public static void init(KafkaConsumer<String, String> consumer,
                          String topic,
                          int partition,
                          long offset,
                          Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    consumer.assign(List.of(tp));
    // must call poll before seek
    consumer.poll(timeout);
    consumer.seek(tp, offset);
  }

  public static Flux<String> pollOnce(KafkaConsumer<String, String> consumer,
                                      String topic,
                                      int partition,
                                      Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    return Mono.fromCallable(() -> consumer.poll(timeout)
                                           .records(tp)
                                           .stream()
                                           .map(ConsumerRecord::value)
                                           .collect(toList())).flatMapMany(Flux::fromIterable);
  }

  //  public static Flux<String> pollEvents(KafkaConsumer<String, String> consumer, TopicPartition tp, Duration timeout) {
  //    return Flux.<List<String>>generate(sink -> {
  //      var list = consumer.poll(timeout).records(tp).stream().map(ConsumerRecord::value).collect(toList());
  //      if (list.size() > 0) {
  //        sink.next(list);
  //      } else {
  //        sink.complete();
  //      }
  //    }).concatMap(Flux::fromIterable);
  //  }

  public static Mono<RecordMetadata> sendRecord(KafkaProducer<String, String> producer,
                                                String topic,
                                                int partition,
                                                String key,
                                                String value,
                                                Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    return Mono.fromCallable(() -> {
      var prodRec = new ProducerRecord<>(tp.topic(), tp.partition(), key, value);
      return producer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    });
  }

  private KafkaUtils() {}
}
