package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.vavr.control.Try;

public class GreetingTransformer implements StringTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return (tClass.equals(Event.class)) ? Try.success((T) new GreetingEvent("0", str))
                                        : Try.failure(new Exception("Unknown type"));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return (t instanceof GreetingEvent ge) ? Try.success(ge.senderName()) : Try.failure(new Exception("Unknown type"));
  }
}
