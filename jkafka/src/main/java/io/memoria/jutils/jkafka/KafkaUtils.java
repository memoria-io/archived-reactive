package io.memoria.jutils.jkafka;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class KafkaUtils {
  public static AdminClient createAdmin(String serverUrl) {
    var config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
    return AdminClient.create(config);
  }

  public static KafkaConsumer<String, String> createConsumer(Map<String, Object> consumerConfig,
                                                             String topic,
                                                             int partition,
                                                             long offset,
                                                             Duration timeout) {
    var consumer = new KafkaConsumer<String, String>(consumerConfig);
    var tp = new TopicPartition(topic, partition);
    consumer.assign(java.util.List.of(tp));
    // must call poll before seek
    consumer.poll(timeout);
    consumer.seek(tp, offset);
    return consumer;
  }

  public static Try<Integer> createTopic(AdminClient admin,
                                         String topic,
                                         int partitions,
                                         short replicationFr,
                                         Duration timeout) {
    return Try.of(() -> admin.createTopics(List.of(new NewTopic(topic, partitions, replicationFr)).toJavaList())
                             .numPartitions(topic)
                             .get(timeout.toMillis(), MILLISECONDS));
  }

  public static Try<Void> increasePartitionsTo(AdminClient admin, String topic, int partitions, Duration timeout) {
    return Try.of(() -> admin.createPartitions(Map.of(topic, NewPartitions.increaseTo(partitions)))
                             .all()
                             .get(timeout.toMillis(), MILLISECONDS));
  }

  public static Try<Long> currentOffset(AdminClient admin, String topic, int partition, Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    return Try.of(() -> admin.listOffsets(Map.of(tp, OffsetSpec.latest()))
                             .partitionResult(tp)
                             .get(timeout.toMillis(), MILLISECONDS)
                             .offset());
  }

  public static Try<String> lastMessage(Map<String, Object> consumerConfig,
                                        String topic,
                                        int partition,
                                        Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    // Create admin and consumer
    var url = consumerConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
    var admin = createAdmin(url);
    var consumer = new KafkaConsumer<String, String>(consumerConfig);
    // Seek and Fetch
    consumer.assign(List.of(tp).toJavaList());
    var tryOffset = currentOffset(admin, topic, partition, timeout);
    return tryOffset.map(lastOffset -> {
      consumer.seek(tp, lastOffset - 2);
      var polledList = pollOnce(consumer, topic, partition, timeout);
      return List.ofAll(polledList);
    }).flatMap(list -> list.lastOption().toTry());
  }

  public static Option<Integer> nPartitions(AdminClient admin, String topic, Duration timeout)
          throws InterruptedException, ExecutionException, TimeoutException {
    var topics = List.of(topic).toJavaList();
    var map = admin.describeTopics(topics).all().get(timeout.toMillis(), MILLISECONDS);
    return Option.of(map.get(topic)).map(opt -> opt.partitions().size());
  }

  public static List<String> pollOnce(KafkaConsumer<String, String> consumer,
                                      String topic,
                                      int partition,
                                      Duration timeout) {
    var tp = new TopicPartition(topic, partition);
    var list = List.ofAll(consumer.poll(timeout).records(tp));
    return list.map(ConsumerRecord::value);
  }

  public static Try<Long> sendRecords(KafkaProducer<String, String> producer,
                                      String topic,
                                      int partition,
                                      List<String> msgs,
                                      Duration timeout) {
    var offset = Option.<Long>none();
    producer.beginTransaction();
    var tp = new TopicPartition(topic, partition);
    try {
      for (String msg : msgs) {
        var rec = new ProducerRecord<String, String>(tp.topic(), tp.partition(), null, msg);
        var meta = producer.send(rec).get(timeout.toMillis(), MILLISECONDS);
        if (meta.hasOffset())
          offset = Option.some(meta.offset());
      }
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      producer.abortTransaction();
      return Try.failure(e);
    }
    producer.commitTransaction();
    return offset.toTry();
  }

  public static Try<Boolean> topicExists(AdminClient admin, String topic, int partition, Duration timeout) {
    return Try.of(() -> admin.describeTopics(List.of(topic).toJavaList())
                             .all()
                             .get(timeout.toMillis(), MILLISECONDS)
                             .get(topic)
                             .partitions()
                             .stream()
                             .anyMatch(p -> p.partition() == partition));
  }

  private KafkaUtils() {}
}
