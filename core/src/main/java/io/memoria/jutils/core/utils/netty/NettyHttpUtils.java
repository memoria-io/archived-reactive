package io.memoria.jutils.core.utils.netty;

import io.memoria.jutils.core.utils.http.HttpUtils;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class NettyHttpUtils {
  private NettyHttpUtils() {}

  public static NettyOutbound send(HttpServerResponse resp, int status, String s) {
    return resp.status(status).sendString(Mono.just(s));
  }

  public static NettyOutbound sendError(HttpServerResponse resp, NettyHttpError nhe) {
    HttpHeaders header = nhe.httpHeaders.isDefined() ? nhe.httpHeaders.get() : EmptyHttpHeaders.INSTANCE;
    return resp.status(nhe.statusCode)
               .headers(resp.responseHeaders().add(header))
               .sendString(Mono.just(nhe.message.getOrElse("Error message unavailable.")));
  }

  public static Try<Tuple2<String, String>> basicAuth(HttpServerRequest req) {
    return Option.of(req.requestHeaders().get("Authorization")).toTry().flatMap(HttpUtils::basicAuth);
  }
}
