package io.memoria.reactive.eventsourcing.netty;

import io.netty.handler.codec.http.HttpResponseStatus;

public interface Response {
  String payload();

  HttpResponseStatus status();

  static Response of(HttpResponseStatus status, String payload) {
    return new DefaultResponse(status, payload);
  }
}
