package io.memoria.jutils.jkafka;

import io.vavr.control.Option;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

public class KafkaUtils {
  public static AdminClient adminClient(String serverUrl) {
    var config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
    return AdminClient.create(config);
  }

  public static int createKafkaTopic(AdminClient admin,
                                     String topic,
                                     int partitions,
                                     short replicationFr,
                                     Duration timeout)
          throws InterruptedException, ExecutionException, TimeoutException {
    admin.createTopics(List.of(new NewTopic(topic, partitions, replicationFr)))
         .numPartitions(topic)
         .get(timeout.toMillis(), MILLISECONDS);
    return partitions;
  }

  public static int currentOffset(AdminClient admin, String topic, int partition, Duration timeout)
          throws InterruptedException, ExecutionException, TimeoutException {
    var tp = new TopicPartition(topic, partition);
    return (int) admin.listOffsets(Map.of(tp, OffsetSpec.latest()))
                      .partitionResult(tp)
                      .get(timeout.toMillis(), MILLISECONDS)
                      .offset();
  }

  public static KafkaConsumer<String, String> init(KafkaConsumer<String, String> consumer,
                                                   String topic,
                                                   int partition,
                                                   long offset,
                                                   Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    consumer.assign(List.of(tp));
    // must call poll before seek
    consumer.poll(timeout);
    consumer.seek(tp, offset);
    return consumer;
  }

  public static Option<String> lastMessage(AdminClient admin,
                                           KafkaConsumer<String, String> consumer,
                                           String topic,
                                           int partition,
                                           Duration timeout)
          throws InterruptedException, ExecutionException, TimeoutException {
    var lastOffset = currentOffset(admin, topic, partition, timeout);
    var tp = new TopicPartition(topic, partition);
    consumer.assign(List.of(tp));
    consumer.seek(tp, lastOffset - 2);
    var list = pollOnce(consumer, topic, partition, timeout);
    return io.vavr.collection.List.ofAll(list).lastOption();
  }

  public static Option<Integer> nPartitions(AdminClient admin, String topic, Duration timeout)
          throws InterruptedException, ExecutionException, TimeoutException {
    var map = admin.describeTopics(List.of(topic)).all().get(timeout.toMillis(), MILLISECONDS);
    return Option.of(map.get(topic)).map(opt -> opt.partitions().size());
  }

  public static List<String> pollOnce(KafkaConsumer<String, String> consumer,
                                      String topic,
                                      int partition,
                                      Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    return consumer.poll(timeout).records(tp).stream().map(ConsumerRecord::value).collect(toList());
  }

  public static long sendRecord(KafkaProducer<String, String> producer,
                                String topic,
                                int partition,
                                String value,
                                Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
    var tp = new TopicPartition(topic, partition);
    var prodRec = new ProducerRecord<String, String>(tp.topic(), tp.partition(), null, value);
    return producer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS).offset();
  }

  public static Boolean topicExists(AdminClient admin, String topic) throws InterruptedException, ExecutionException {
    ListTopicsResult listTopics = admin.listTopics();
    var names = listTopics.names().get();
    return names.contains(topic);
  }

  private KafkaUtils() {}
}
