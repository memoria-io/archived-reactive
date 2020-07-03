package io.memoria.jutils.messaging.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.time.LocalDateTime;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Response(Option<String>value, Option<LocalDateTime>creationTime, Option<Provider>provider,
                       Map<String, String>extra) {
  public static Response empty() {
    return new Response(none(), none(), none(), HashMap.empty());
  }

  public Response() {
    this(none(), none(), none(), HashMap.empty());
  }

  public Response(long value) {
    this(String.valueOf(value));
  }

  public Response(String value) {
    this(some(value), none(), none(), HashMap.empty());
  }

  public boolean isEmpty() {
    return !value.isDefined() && !creationTime.isDefined() && !provider.isDefined() && extra.isEmpty();
  }

  public Response withMeta(String k, String v) {
    return new Response(value, creationTime, provider, extra.put(k, v));
  }

  public Response withProvider(Provider provider) {
    return new Response(value, creationTime, some(provider), extra);
  }
}