package io.memoria.jutils.core.messaging;

import io.vavr.Function1;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgSender extends Function1<Flux<Message>, Flux<Response>> {}
