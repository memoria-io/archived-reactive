package com.marmoush.jutils.utils.netty;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;

public class NettyHttpError {
  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public final int statusCode;
  public final Option<String> message;
  public final Option<HttpHeaders> httpHeaders;

  public NettyHttpError(Throwable t, int statusCode) {
    this(t, statusCode, none());
  }

  public NettyHttpError(Throwable t, int statusCode, Option<HttpHeaders> httpHeaders) {
    this.statusCode = statusCode;
    this.message = Option.of(t.getMessage());
    this.httpHeaders = httpHeaders;
  }
}
