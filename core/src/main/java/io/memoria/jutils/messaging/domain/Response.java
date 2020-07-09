package io.memoria.jutils.messaging.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.time.LocalDateTime;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Response(Option<String>value, Option<LocalDateTime>creationTime, Map<String, String>meta) {
  public static Response empty() {
    return new Response(none(), none(), HashMap.empty());
  }

  public Response(long value) {
    this(String.valueOf(value));
  }

  public Response(String value) {
    this(some(value), none(), HashMap.empty());
  }

  public boolean isEmpty() {
    return !value.isDefined() && !creationTime.isDefined() && meta.isEmpty();
  }

  public Response withMeta(String k, String v) {
    return new Response(value, creationTime, meta.put(k, v));
  }
}
