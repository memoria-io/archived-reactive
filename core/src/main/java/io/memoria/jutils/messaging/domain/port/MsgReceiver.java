package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgReceiver {
  Flux<Message> receive(String topicId, int partition, long offset);
}
