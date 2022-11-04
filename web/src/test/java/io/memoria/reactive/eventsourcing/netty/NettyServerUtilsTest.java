package io.memoria.reactive.eventsourcing.netty;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static io.memoria.reactive.eventsourcing.netty.NettyServerUtils.statusReply;
import static io.memoria.reactive.eventsourcing.netty.NettyServerUtils.stringReply;
import static io.memoria.reactive.eventsourcing.netty.TestUtils.httpClient;
import static io.memoria.reactive.eventsourcing.netty.TestUtils.httpServer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

class NettyServerUtilsTest {
  private static final String stringReplyPath = "/string";
  private static final String statusReplyPath = "/status";
  private static final String errorReplyPath = "/error";
  private static final DisposableServer disposableServer;

  static {
    disposableServer = httpServer.route(routes()).bindNow();
  }

  @Test
  void errorReplyTest() {
    var monoResp = NettyClientUtils.get(httpClient, errorReplyPath);
    StepVerifier.create(monoResp).expectNext(Response.of(UNAUTHORIZED, "Unauthorized")).expectComplete().verify();
  }

  @Test
  void statusReplyTest() {
    var monoResp = NettyClientUtils.get(httpClient, statusReplyPath);
    StepVerifier.create(monoResp)
                .expectNext(Response.of(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()))
                .expectComplete()
                .verify();
  }

  @Test
  void stringReplyTest() {
    var monoResp = NettyClientUtils.get(httpClient, stringReplyPath);
    StepVerifier.create(monoResp).expectNext(Response.of(OK, "Hello")).expectComplete().verify();
  }

  @AfterAll
  static void afterAll() {
    disposableServer.dispose();
  }

  private static Consumer<HttpServerRoutes> routes() {
    return r -> r.get(statusReplyPath, (req, resp) -> statusReply.apply(resp).apply(UNAUTHORIZED))
                 .get(stringReplyPath, (req, resp) -> stringReply.apply(resp).apply(OK, "Hello"))
                 .get(errorReplyPath,
                      (req, resp) -> stringReply.apply(resp).apply(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()));
  }
}
