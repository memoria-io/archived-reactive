package dummy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerTest {

  public static void main(String[] args) throws InterruptedException {
    Producer<String, String> producer = createProducer();
    sendMessages(producer);
    // Allow the producer to complete sending of the messages before program exit.
    Thread.sleep(20);
  }

  private static Producer<String, String> createProducer() {
    Properties props = new Properties();
    props.put("bootstrap.servers", "172.17.0.1:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    // Controls how much bytes sender would wait to batch up before publishing to Kafka.
    props.put("batch.size", 10);
    props.put("linger.ms", 1);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    return new KafkaProducer(props);
  }

  private static void sendMessages(Producer<String, String> producer) {
    String topic = "normal-topic";
    int partition = 0;
    long record = 1000;
    for (int i = 1; i <= 1000; i++) {
      var rec = new ProducerRecord<>(topic, partition, Long.toString(record), "Hello: " + record++ + " ");
      producer.send(rec);
    }
  }
}
