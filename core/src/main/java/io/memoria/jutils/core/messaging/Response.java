package io.memoria.jutils.core.messaging;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Response(Option<Long>id, Option<String>reply, Map<String, String>meta) {
  public static Response empty() {
    return new Response();
  }

  public Response() {
    this(none(), none(), HashMap.empty());
  }

  public Response(long id) {
    this(some(id), none(), HashMap.empty());
  }

  public boolean isEmpty() {
    return !id.isDefined() && !reply.isDefined() && meta.isEmpty();
  }

  public Response withId(long id) {
    return new Response(some(id), this.reply, this.meta);
  }

  public Response withMeta(String k, String v) {
    return new Response(this.id, this.reply, this.meta.put(k, v));
  }

  public Response withReply(String message) {
    return new Response(this.id, some(message), this.meta);
  }
}
