package io.memoria.jutils.jkafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

public class TestConfigs {
  public static final Map<String, Object> producerConf = new HashMap<>();
  public static final Map<String, Object> consumerConf = new HashMap<>();

  static {
    // Producer configs
    producerConf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    producerConf.put(ProducerConfig.ACKS_CONFIG, "all");
    producerConf.put(ProducerConfig.RETRIES_CONFIG, 2);
    producerConf.put(ProducerConfig.BATCH_SIZE_CONFIG, 1);
    producerConf.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    producerConf.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
    producerConf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                     "org.apache.kafka.common.serialization.StringSerializer");
    producerConf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                     "org.apache.kafka.common.serialization.StringSerializer");
    producerConf.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"transaction_id_123456789");
    // Consumer configs
    consumerConf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    consumerConf.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    consumerConf.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 135);
    consumerConf.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
    consumerConf.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
    consumerConf.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
    consumerConf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                     "org.apache.kafka.common.serialization.StringDeserializer");
    consumerConf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                     "org.apache.kafka.common.serialization.StringDeserializer");
  }
}