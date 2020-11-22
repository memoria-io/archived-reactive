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
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    return events.concatMap(e -> sendRecord(topic, e)).subscribeOn(scheduler);
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> {
      ListTopicsResult listTopics = adminClient.listTopics();
      var names = listTopics.names().get();
      return names.contains(topic);
    }).subscribeOn(scheduler);
  }

  @Override
  public Flux<Event> stream(String topic) {
    var tp = new TopicPartition(topic, 0);
    return Mono.fromRunnable(() -> {
      consumer.assign(List.of(tp));
      // must call poll before seek
      consumer.poll(timeout);
      consumer.seek(tp, 0);
    }).thenMany(pollEvents(tp)).subscribeOn(scheduler);
  }

  private Flux<Event> pollEvents(TopicPartition tp) {
    return Flux.<List<Event>>generate(sink -> {
      var list = toEventList(consumer.poll(timeout).records(tp));
      if (list.size() > 0) {
        sink.next(list);
      } else {
        sink.complete();
      }
    }).concatMap(Flux::fromIterable).subscribeOn(scheduler);
  }

  private List<Event> toEventList(List<ConsumerRecord<String, String>> crs) {
    return crs.stream()
              .map(ConsumerRecord::value)
              .map(str -> transformer.deserialize(str, Event.class).get())
              .collect(Collectors.toList());
  }

  private Mono<Event> sendRecord(String topic, Event event) {
    var prodRec = new ProducerRecord<>(topic, 0, event.id().value(), transformer.serialize(event).get());
    return Mono.fromCallable(() -> producer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS))
               .map(e -> event)
               .subscribeOn(scheduler);
  }
}
