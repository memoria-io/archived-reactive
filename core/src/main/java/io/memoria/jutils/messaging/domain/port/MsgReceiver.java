package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@FunctionalInterface
public interface MsgReceiver extends Supplier<Flux<Message>> {}
