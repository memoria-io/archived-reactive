package io.memoria.jutils.jpulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageRouter;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TopicMetadata;
import org.apache.pulsar.client.api.transaction.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PulsarUtils {
  public static Mono<Consumer<String>> createConsumer(PulsarClient client, String topic, int offset) {
    return Mono.fromFuture(client.newConsumer(Schema.STRING)
                                 .topic(topic)
                                 .subscriptionName(topic + "_subscription")
                                 .subscribeAsync()).flatMap(c -> Mono.fromFuture(c.seekAsync(offset)).thenReturn(c));
  }

  public static Mono<Producer<String>> createProducer(PulsarClient client, String topic, int partition) {
    MessageRouter mr = new MessageRouter() {
      @Override
      public int choosePartition(Message<?> msg, TopicMetadata metadata) {
        return partition;
      }
    };
    return Mono.fromFuture(client.newProducer(Schema.STRING).topic(topic).messageRouter(mr).createAsync());
  }

  public static Mono<Transaction> createTransaction(PulsarClient client) {
    return Mono.fromFuture(client.newTransaction().build());
  }

  public static Flux<String> receive(Consumer<String> consumer) {
    return Mono.fromFuture(consumer::receiveAsync).map(Message::getValue).repeat();
  }

  public static Mono<MessageId> send(Producer<String> producer, Transaction tx, String msg) {
    return Mono.fromFuture(producer.newMessage(tx).value(msg).sendAsync());
  }

  public static Flux<MessageId> send(Producer<String> producer, Transaction tx, Flux<String> msgs) {
    return msgs.concatMap(msg -> send(producer, tx, msg));
  }

  private PulsarUtils() {}
}
