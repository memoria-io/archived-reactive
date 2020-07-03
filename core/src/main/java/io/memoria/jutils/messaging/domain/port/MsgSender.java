package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.vavr.Function1;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgSender extends Function1<Flux<Message>, Flux<Response>> {}
