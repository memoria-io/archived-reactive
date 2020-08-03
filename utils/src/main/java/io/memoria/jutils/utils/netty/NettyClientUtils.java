package io.memoria.jutils.utils.netty;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

import java.util.function.Consumer;

public class NettyClientUtils {
  public static Mono<Tuple2<HttpResponseStatus, String>> get(String host, String path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .get()
                     .uri(path)
                     .responseSingle((res, body) -> body.asString().map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> get(String host,
                                                             String path,
                                                             Consumer<HttpHeaders> httpHeaders) {
    return HttpClient.create()
                     .baseUrl(host)
                     .headers(httpHeaders)
                     .get()
                     .uri(path)
                     .responseSingle((res, body) -> body.asString().map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> post(String host, String path, String payload) {
    return HttpClient.create()
                     .baseUrl(host)
                     .post()
                     .uri(path)
                     .send(ByteBufFlux.fromString(Flux.just(payload)))
                     .responseSingle((res, body) -> body.asString().map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> post(String host,
                                                              String path,
                                                              Consumer<HttpHeaders> httpHeaders,
                                                              String payload) {
    return HttpClient.create()
                     .baseUrl(host)
                     .headers(httpHeaders)
                     .post()
                     .uri(path)
                     .send(ByteBufFlux.fromString(Flux.just(payload)))
                     .responseSingle((res, body) -> body.asString().map(s -> Tuple.of(res.status(), s)));
  }

  private NettyClientUtils() {}
}
