package io.memoria.jutils.core.utils.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import static io.memoria.jutils.core.utils.netty.NettyClientUtils.post;
import static io.memoria.jutils.core.utils.netty.NettyServerUtils.stringReply;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

class NettyClientUtilsTest {
  private static final String stringReplyPath = "/string";
  private static final String errorReplyPath = "/error";

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
  void errorReplyTest() {
    var monoResp = post("whatever", host, errorReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(UNAUTHORIZED, "Unauthorized")).expectComplete().verify();
  }

  @Test
  void stringReplyTest() {
    var monoResp = post("payload", host, stringReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, "payload")).expectComplete().verify();
  }

  private static Mono<Void> handlePost(HttpServerRequest req, HttpServerResponse resp) {
    var payload = req.receive().aggregate().asString();
    return payload.flatMap(str -> stringReply.apply(resp).apply(OK).apply(str).then());
  }

  private static Publisher<Void> handlePostWithError(HttpServerRequest req, HttpServerResponse resp) {
    return stringReply.apply(resp).apply(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase());
  }

  private static void routes(HttpServerRoutes routes) {
    routes.post(stringReplyPath, NettyClientUtilsTest::handlePost)
          .post(errorReplyPath, NettyClientUtilsTest::handlePostWithError);
  }
}
