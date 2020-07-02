package io.memoria.jutils.core.utils.netty;

import io.memoria.jutils.core.utils.http.HttpUtils;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import static io.vavr.control.Option.none;

public class NettyHttpUtils {
  public static final HttpHeaders AUTH_CHALLENGES = new DefaultHttpHeaders().add("WWW-Authenticate", "Basic, Bearer");

  public static Option<Tuple2<String, String>> basicFrom(HttpServerRequest req) {
    return Option.of(req.requestHeaders().get("Authorization")).flatMap(HttpUtils::basicFrom);
  }

  public static NettyOutbound send(HttpServerResponse resp, HttpResponseStatus status, Option<String> message) {
    return resp.status(status).sendString(Mono.just(message.getOrElse(status.reasonPhrase())));
  }

  public static NettyOutbound send(HttpServerResponse resp, HttpResponseStatus status) {
    return send(resp, status, none());
  }

  public static NettyOutbound sendError(HttpServerResponse resp, NettyHttpError nhe) {
    return resp.status(nhe.status())
               .headers(resp.responseHeaders().add(nhe.httpHeaders().getOrElse(EmptyHttpHeaders.INSTANCE)))
               .sendString(Mono.just(nhe.message().getOrElse(nhe.status().reasonPhrase())));
  }

  public static Option<String> tokenFrom(HttpServerRequest req) {
    return Option.of(req.requestHeaders().get("Authorization")).flatMap(HttpUtils::tokenFrom);
  }

  private NettyHttpUtils() {}
}
