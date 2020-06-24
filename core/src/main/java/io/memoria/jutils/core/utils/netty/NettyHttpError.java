package io.memoria.jutils.core.utils.netty;

import io.memoria.jutils.core.utils.http.StatusCode;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;

public record NettyHttpError(StatusCode statusCode, String message, Option<HttpHeaders>httpHeaders) {
  public NettyHttpError(StatusCode statusCode, String message) {
    this(statusCode, message, none());
  }
}
