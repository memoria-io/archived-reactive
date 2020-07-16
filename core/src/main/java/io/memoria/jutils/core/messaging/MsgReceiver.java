package io.memoria.jutils.core.messaging;

import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@FunctionalInterface
public interface MsgReceiver extends Supplier<Flux<Message>> {}
