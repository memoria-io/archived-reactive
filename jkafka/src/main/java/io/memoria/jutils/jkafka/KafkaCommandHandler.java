//package io.memoria.jutils.jkafka;
//
//import io.memoria.jutils.jcore.eventsourcing.Command;
//import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
//import io.memoria.jutils.jcore.eventsourcing.Decider;
//import io.memoria.jutils.jcore.eventsourcing.Event;
//import io.memoria.jutils.jcore.eventsourcing.Evolver;
//import io.memoria.jutils.jcore.id.Id;
//import io.memoria.jutils.jcore.text.TextTransformer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Scheduler;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class KafkaCommandHandler {
//  public static <S, C extends Command> Mono<CommandHandler<S, C>> create(Map<String, Object> producerConfig,
//                                                                         Map<String, Object> consumerConfig,
//                                                                         String topic,
//                                                                         int partition,
//                                                                         int replicationFr,
//                                                                         TextTransformer transformer,
//                                                                         S initState,
//                                                                         Decider<S, C> decider,
//                                                                         Evolver<S> evolver,
//                                                                         Duration reqTimeout,
//                                                                         Scheduler scheduler) {
//    var eventStore = new KafkaEventStore(producerConfig,
//                                         consumerConfig,
//                                         topic,
//                                         partition,
//                                         transformer,
//                                         reqTimeout,
//                                         scheduler);
//    var url = producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
//    var admin = new KafkaAdmin(url, reqTimeout, scheduler);
//    ConcurrentHashMap<Id, S> stateStore = new ConcurrentHashMap<>();
//    return admin.exists(topic, partition).flatMap(b -> {
//      if (b) {
//        return evolve(eventStore.subscribeToLast(), evolver, stateStore).then();
//      } else {
//        return admin.createTopic(topic, partition, replicationFr);
//      }
//    }).then(Mono.just(new CommandHandler<S, C>(initState, stateStore, eventStore, decider, evolver)));
//  }
//
//  private static <S> Flux<S> evolve(Flux<Event> events, Evolver<S> evolver, ConcurrentHashMap<Id, S> stateStore) {
//    return events.map(event -> stateStore.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)));
//  }
//
//  private KafkaCommandHandler() {}
//}
