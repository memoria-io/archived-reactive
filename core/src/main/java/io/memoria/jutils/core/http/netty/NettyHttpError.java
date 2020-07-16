package io.memoria.jutils.core.http.netty;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record NettyHttpError(HttpResponseStatus status, Option<String>message, Option<HttpHeaders>httpHeaders) {
  public NettyHttpError(HttpResponseStatus status, String message) {
    this(status, some(message), none());
  }

  public NettyHttpError(HttpResponseStatus status) {
    this(status, none(), none());
  }
}
