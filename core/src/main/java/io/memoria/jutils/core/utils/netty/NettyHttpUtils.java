package io.memoria.jutils.core.utils.netty;

import io.memoria.jutils.core.utils.http.HttpUtils;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.Tuple2;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class NettyHttpUtils {
  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public static NettyOutbound send(HttpServerResponse resp, int status, String message) {
    return resp.status(status).sendString(Mono.just(message));
  }

  public static NettyOutbound sendError(HttpServerResponse resp, int code, String message) {
    return resp.status(code).sendString(Mono.just(message));
  }

  public static NettyOutbound sendError(HttpServerResponse resp, int code, String message, HttpHeaders headers) {
    return resp.status(code).headers(resp.responseHeaders().add(headers)).sendString(Mono.just(message));
  }

  public static NettyOutbound sendError(HttpServerResponse resp, NettyHttpError nhe) {
    return resp.status(nhe.statusCode().code)
               .headers(resp.responseHeaders().add(nhe.httpHeaders().getOrElse(EmptyHttpHeaders.INSTANCE)))
               .sendString(Mono.just(nhe.message()));
  }

  public static Tuple2<String, String> basicCredentials(HttpServerRequest req) {
    String auth = req.requestHeaders().get("Authorization");
    return HttpUtils.toBasicAuthCredentials(auth);
  }

  private NettyHttpUtils() {}
}
