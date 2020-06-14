package io.memoria.jutils.core.utils.netty;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;

public record NettyHttpError(Throwable t, int statusCode, String message, HttpHeaders httpHeaders) {
  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public NettyHttpError(Throwable t, int statusCode) {
    this(t, statusCode, t.getMessage(), EmptyHttpHeaders.INSTANCE);
  }

  public NettyHttpError(Throwable t, int statusCode, String message) {
    this(t, statusCode, message, EmptyHttpHeaders.INSTANCE);
  }

  public NettyHttpError(Throwable t, int statusCode, HttpHeaders httpHeaders) {
    this(t, statusCode, t.getMessage(), httpHeaders);
  }
}
