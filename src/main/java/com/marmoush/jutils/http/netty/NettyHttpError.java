package com.marmoush.jutils.http.netty;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vavr.control.Option.none;

public class NettyHttpError {
  private static final Logger log = LoggerFactory.getLogger(NettyHttpError.class.getName());

  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public final int statusCode;
  public final Option<String> message;
  public final Option<HttpHeaders> httpHeaders;

  public NettyHttpError(Throwable t, int statusCode) {
    this(t, statusCode, none());
  }

  public NettyHttpError(Throwable t, int statusCode, Option<HttpHeaders> httpHeaders) {
    log.error("An error occured w" + statusCode, t);
    this.statusCode = statusCode;
    this.message = Option.of(t.getMessage());
    this.httpHeaders = httpHeaders;
  }
}
