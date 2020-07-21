package io.memoria.jutils.core.http.netty;

import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.Function2;
import io.vavr.Function3;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;

public class NettyServerUtils {
  public static final Function2<HttpServerResponse, NettyHttpError, NettyOutbound> errorReply = (resp, nettyError) -> resp
          .status(nettyError.status())
          .headers(resp.responseHeaders().add(nettyError.httpHeaders().getOrElse(EmptyHttpHeaders.INSTANCE)))
          .sendString(Mono.just(nettyError.message().getOrElse(nettyError.status().reasonPhrase())));

  public static final Function2<HttpServerResponse, HttpResponseStatus, NettyOutbound> statusReply = (resp, status) -> resp
          .status(status)
          .sendString(Mono.just(status.reasonPhrase()));

  public static final Function3<HttpServerResponse, HttpResponseStatus, String, NettyOutbound> stringReply = (resp, status, message) -> resp
          .status(status)
          .sendString(Mono.just(message));

  private NettyServerUtils() {}
}
