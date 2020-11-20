package io.memoria.jutils.kafka.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.transformer.StringTransformer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaEventStore implements EventStore {
  private final KafkaConsumer<String, String> consumer;
  private final KafkaProducer<String, String> producer;
  private final Duration timeout;
  private final Scheduler scheduler;
  private final StringTransformer transformer;
  private final AdminClient adminClient;

  public KafkaEventStore(Map<String, Object> producerConfig,
                         Map<String, Object> consumerConfig,
                         Duration timeout,
                         Scheduler scheduler,
                         StringTransformer transformer) {
    this.consumer = new KafkaConsumer<>(consumerConfig);
    this.producer = new KafkaProducer<>(producerConfig);
    this.timeout = timeout;
    this.scheduler = scheduler;
    this.transformer = transformer;
    // Setup admin client
    Properties config = new Properties();
    var serverURL = producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverURL);
    adminClient = AdminClient.create(config);
  }

  @Override
  public Flux<Event> add(String topic, Flux<Event> events) {
    return events.concatMap(e -> sendRecord(topic, e));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> {
      ListTopicsResult listTopics = adminClient.listTopics();
      var names = listTopics.names().get();
      return names.contains(topic);
    });
  }

  @Override
  public Flux<Event> stream(String topic) {
    var tp = new TopicPartition(topic, 0);
    return Mono.fromRunnable(() -> {
      consumer.assign(List.of(tp));
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(tp, 0);
    }).thenMany(Flux.<Flux<Event>>generate(c -> c.next(pollOnce(tp))).concatMap(f -> f).subscribeOn(scheduler));
  }

  private Flux<Event> pollOnce(TopicPartition tp) {
    return Mono.fromCallable(() -> consumer.poll(timeout))
               .flux()
               .concatMap(crs -> Flux.fromIterable(crs.records(tp)))
               .map(ConsumerRecord::value)
               .map(str -> transformer.deserialize(str, Event.class).get());
  }

  private Mono<Event> sendRecord(String topic, Event event) {
    var prodRec = new ProducerRecord<>(topic, 0, event.id().value(), transformer.serialize(event).get());
    return Mono.<RecordMetadata>create(sink -> producer.send(prodRec, (metadata, e) -> {
      if (metadata != null)
        sink.success(metadata);
      else
        sink.error(e);
    })).subscribeOn(scheduler).map(e -> event);
  }
}
