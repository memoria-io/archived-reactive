package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.MsgStream;
import reactor.core.publisher.Flux;

import java.util.List;

public record MemMsgStream(List<Msg> store) implements MsgStream {

  @Override
  public Flux<Msg> publish(Flux<Msg> msgs) {
    return msgs.map(msg -> {
      store.add(msg);
      return msg;
    });
  }

  @Override
  public Flux<Msg> subscribe(long offset) {
    return Flux.fromIterable(store).skip(offset);
  }
}
