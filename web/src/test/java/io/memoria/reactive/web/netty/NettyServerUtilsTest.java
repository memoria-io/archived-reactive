package io.memoria.reactive.web.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

class NettyServerUtilsTest {
  private static final String stringReplyPath = "/string";
  private static final String statusReplyPath = "/status";
  private static final String errorReplyPath = "/error";

  private static final String host = "127.0.0.1:8082";
  private static final DisposableServer server = HttpServer.create()
                                                           .host("127.0.0.1")
                                                           .port(8082)
                                                           .route(NettyServerUtilsTest::routes)
                                                           .bindNow();

  @AfterAll
  static void afterAll() {
    server.dispose();
  }

  @Test
  void errorReplyTest() {
    var monoResp = NettyClientUtils.get(host, errorReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(UNAUTHORIZED, "Unauthorized")).expectComplete().verify();
  }

  @Test
  void statusReplyTest() {
    var monoResp = NettyClientUtils.get(host, statusReplyPath);
    StepVerifier.create(monoResp)
                .expectNext(Tuple.of(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()))
                .expectComplete()
                .verify();
  }

  @Test
  void stringReplyTest() {
    var monoResp = NettyClientUtils.get(host, stringReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, "Hello")).expectComplete().verify();
  }

  private static void routes(HttpServerRoutes routes) {
    routes.get(statusReplyPath, (req, resp) -> NettyServerUtils.statusReply.apply(resp).apply(UNAUTHORIZED))
          .get(stringReplyPath, (req, resp) -> NettyServerUtils.stringReply.apply(resp).apply(OK, "Hello"))
          .get(errorReplyPath,
               (req, resp) -> NettyServerUtils.stringReply.apply(resp)
                                                          .apply(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()));
  }
}
