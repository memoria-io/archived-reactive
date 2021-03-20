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
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

class KafkaStreamsTest {

  private static final Scheduler scheduler = Schedulers.boundedElastic();
  private static final Duration timeout = Duration.of(1, SECONDS);
  //  private static final int i = new Random().nextInt(1000);
  private static final int i = 961;
  private static final String INPUT_TOPIC = "streams-plaintext-input" + i;
  private static final String OUTPUT_TOPIC = "streams-plaintext-output" + i;
  private final KafkaConsumer<String, String> consumer;
  private final KafkaProducer<String, String> producer;
  private final AdminClient adminClient;

  KafkaStreamsTest() {
    this.consumer = new KafkaConsumer<>(TestConfigs.consumerConf);
    this.producer = new KafkaProducer<>(TestConfigs.producerConf);
    // Setup admin client
    Properties config = new Properties();
    var serverURL = TestConfigs.producerConf.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverURL);
    adminClient = AdminClient.create(config);
  }

  public static void main(String[] args) {
    final Serde<String> stringSerde = Serdes.String();
    final Serde<Long> longSerde = Serdes.Long();
    Properties properties = new Properties();
    properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "demo-kafka-streams");
    properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
    properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

    StreamsBuilder streamsBuilder = new StreamsBuilder();
    KStream<String, String> textLines = streamsBuilder.stream(INPUT_TOPIC, Consumed.with(stringSerde, stringSerde));
    KTable<String, Long> wordCounts = textLines.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                                               .groupBy((key, value) -> value)
                                               .count();
    wordCounts.toStream().to(OUTPUT_TOPIC, Produced.with(stringSerde, longSerde));
    
    KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
    kafkaStreams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
      throwable.printStackTrace();
    });
    kafkaStreams.start();
  }

//  @Test
//  void testKafkaStream() {
//
//    //    StepVerifier.create(stream(INPUT_TOPIC).doOnNext(System.out::println))
//    //                .expectNextCount(10)
//    //                .expectComplete()
//    //                .verify();
//
//    var f = Flux.interval(ofMillis(100))
//                .take(10)
//                .concatMap(i -> sendRecord(INPUT_TOPIC,
//                                           i + "",
//                                           "all streams lead to kafka\nhello kafka streams\njoin kafka summit" + i));
//    StepVerifier.create(f).expectNextCount(10).expectComplete().verify();
//    //    StepVerifier.create(stream(OUTPUT_TOPIC).doOnNext(System.out::println))
//    //                .expectNextCount(10)
//    //                .expectComplete()
//    //                .verify();
//  }

  private Flux<String> pollEvents(String topic) {
    var tp = new TopicPartition(topic, 0);
    return Flux.<List<String>>generate(sink -> {
      var list = toEventList(consumer.poll(timeout).records(tp));
      if (list.size() > 0) {
        sink.next(list);
      } else {
        sink.complete();
      }
    }).concatMap(Flux::fromIterable).subscribeOn(scheduler);
  }

  private Mono<RecordMetadata> sendRecord(String topic, String key, String value) {
    var prodRec = new ProducerRecord<>(topic, 0, key, value);
    return Mono.fromCallable(() -> producer.send(prodRec).get(1, TimeUnit.SECONDS)).subscribeOn(scheduler);
  }

  private Flux<String> stream(String topic) {
    var tp = new TopicPartition(topic, 0);
    return Mono.fromRunnable(() -> {
      consumer.assign(List.of(tp));
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(tp, 0);
    }).thenMany(pollEvents(topic)).subscribeOn(scheduler);
  }

  private List<String> toEventList(List<ConsumerRecord<String, String>> crs) {
    return crs.stream().map(ConsumerRecord::value).collect(Collectors.toList());
  }

}
/*
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --topic streams-plaintext-output961 \
        --from-beginning --partition 0 \
        --formatter kafka.tools.DefaultMessageFormatter \
        --property print.key=true \
        --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
        
 */