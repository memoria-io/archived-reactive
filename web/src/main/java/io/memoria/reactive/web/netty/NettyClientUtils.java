package io.memoria.reactive.web.netty;

import io.memoria.reactive.web.http.HttpUtils;
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
  private NettyClientUtils() {}

  public static Mono<Tuple2<HttpResponseStatus, String>> delete(String host, String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .delete()
                     .uri(HttpUtils.joinPath(path))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> get(String host, String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .get()
                     .uri(HttpUtils.joinPath(path))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> get(Consumer<HttpHeaders> httpHeaders,
                                                             String host,
                                                             String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .headers(httpHeaders)
                     .get()
                     .uri(HttpUtils.joinPath(path))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> post(String payload, String host, String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .post()
                     .uri(HttpUtils.joinPath(path))
                     .send(ByteBufFlux.fromString(Flux.just(payload)))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> post(String payload,
                                                              Consumer<HttpHeaders> httpHeaders,
                                                              String host,
                                                              String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .headers(httpHeaders)
                     .post()
                     .uri(HttpUtils.joinPath(path))
                     .send(ByteBufFlux.fromString(Flux.just(payload)))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }

  public static Mono<Tuple2<HttpResponseStatus, String>> put(String payload, String host, String... path) {
    return HttpClient.create()
                     .baseUrl(host)
                     .put()
                     .uri(HttpUtils.joinPath(path))
                     .send(ByteBufFlux.fromString(Flux.just(payload)))
                     .responseSingle((res, body) -> body.asString()
                                                        .defaultIfEmpty("")
                                                        .map(s -> Tuple.of(res.status(), s)));
  }
}
