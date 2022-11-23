package io.memoria.reactive.kafka;

import io.memoria.atom.core.id.Id;
import io.memoria.reactive.eventsourcing.repo.Msg;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

class KafkaUtils {
  private KafkaUtils() {}

  public static Msg toMsg(ConsumerRecord<String, String> record) {
    return new Msg(record.topic(), record.partition(), Id.of(record.key()), record.value());
  }

  public static long topicSize(String topic, int partition, Map<String, Object> conf) {
    try (var consumer = new KafkaConsumer<Long, String>(conf.toJavaMap())) {
      var tp = new TopicPartition(topic, partition);
      var tpCol = List.of(tp).toJavaList();
      consumer.assign(tpCol);
      consumer.seekToEnd(tpCol);
      return consumer.position(tp);
    }
  }
}
