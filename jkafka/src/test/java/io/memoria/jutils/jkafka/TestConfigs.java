package io.memoria.jutils.jkafka;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import static io.vavr.collection.HashMap.*;

public class TestConfigs {
  public static final Map<String, Object> producerConf;
  public static final Map<String, Object> consumerConf;

  static {
    // Producer configs
    var pConf = new java.util.HashMap<String, Object>();
    pConf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    pConf.put(ProducerConfig.ACKS_CONFIG, "all");
    pConf.put(ProducerConfig.RETRIES_CONFIG, 2);
    pConf.put(ProducerConfig.BATCH_SIZE_CONFIG, 1);
    pConf.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    pConf.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
    pConf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    pConf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    producerConf = ofAll(pConf);

    // Consumer configs
    var cConf = new java.util.HashMap<String, Object>();
    cConf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    cConf.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    cConf.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 135);
    cConf.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
    cConf.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
    cConf.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
    cConf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    cConf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
              "org.apache.kafka.common.serialization.StringDeserializer");
    consumerConf = ofAll(cConf);
  }
}