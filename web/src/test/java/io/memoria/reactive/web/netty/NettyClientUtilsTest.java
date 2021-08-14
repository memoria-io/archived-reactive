package io.memoria.reactive.web.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

class NettyClientUtilsTest {
  private static final String endpoint = "/endpoint";
  private static final String ping = "ping";
  private static final String pong = "pong";

  private static final String host = "127.0.0.1:8082";
  private static final DisposableServer server = HttpServer.create()
                                                           .host("127.0.0.1")
                                                           .port(8082)
                                                           .route(NettyClientUtilsTest::routes)
                                                           .bindNow();

  @AfterAll
  static void afterAll() {
    server.dispose();
  }

  @Test
  void deleteTest() {
    var monoResp = NettyClientUtils.get(host, endpoint);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, pong)).expectComplete().verify();
  }

  @Test
  void getTest() {
    var monoResp = NettyClientUtils.get(host, endpoint);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, pong)).expectComplete().verify();
  }

  @Test
  void notFoundTest() {
    var monoResp = NettyClientUtils.post(ping, host, "someUndefinedPath");
    StepVerifier.create(monoResp).expectNext(Tuple.of(NOT_FOUND, "")).expectComplete().verify();
  }

  @Test
  void postTest() {
    var monoResp = NettyClientUtils.post(ping, host, endpoint);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, ping + pong)).expectComplete().verify();
  }

  @Test
  void putTest() {
    var monoResp = NettyClientUtils.put(ping, host, endpoint);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, ping + pong)).expectComplete().verify();
  }

  private static Mono<Void> handle(HttpServerRequest req, HttpServerResponse resp) {
    return NettyServerUtils.stringReply.apply(resp).apply(OK).apply(pong).then();
  }

  private static Mono<Void> handlePayload(HttpServerRequest req, HttpServerResponse resp) {
    return req.receive()
              .aggregate()
              .asString()
              .flatMap(ping -> NettyServerUtils.stringReply.apply(resp).apply(OK).apply(ping + pong).then());
  }

  private static void routes(HttpServerRoutes routes) {
    routes.get(endpoint, NettyClientUtilsTest::handle)
          .post(endpoint, NettyClientUtilsTest::handlePayload)
          .put(endpoint, NettyClientUtilsTest::handlePayload)
          .delete(endpoint, NettyClientUtilsTest::handle);
  }
}
