package io.memoria.jutils.core.http.netty;

import io.memoria.jutils.core.http.HttpUtils;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import reactor.netty.http.server.HttpServerRequest;

public class NettyAuthUtils {
  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public static Option<Tuple2<String, String>> basicAuth(HttpServerRequest req) {
    return Option.of(req.requestHeaders().get("Authorization")).flatMap(HttpUtils::basicFrom);
  }

  public static Option<String> bearerAuth(HttpServerRequest req) {
    return Option.of(req.requestHeaders().get("Authorization")).flatMap(HttpUtils::tokenFrom);
  }

  private NettyAuthUtils() {}
}
