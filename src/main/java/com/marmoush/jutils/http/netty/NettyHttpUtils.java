package com.marmoush.jutils.http.netty;

import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;

public class NettyHttpUtils {
  private NettyHttpUtils() {}

  public static NettyOutbound send(HttpServerResponse resp, int status, String s) {
    return send(resp, status, s, EmptyHttpHeaders.INSTANCE);
  }

  public static NettyOutbound send(HttpServerResponse resp, int status, String s, HttpHeaders headers) {
    return resp.status(status).headers(headers).sendString(Mono.just(s));
  }

  public static NettyOutbound sendError(HttpServerResponse resp, NettyHttpError nhe) {
    HttpHeaders header = nhe.httpHeaders.isDefined() ? nhe.httpHeaders.get() : EmptyHttpHeaders.INSTANCE;
    return resp.status(nhe.statusCode)
               .headers(resp.responseHeaders().add(header))
               .sendString(Mono.just(nhe.message.getOrElse("Error")));
  }
}
