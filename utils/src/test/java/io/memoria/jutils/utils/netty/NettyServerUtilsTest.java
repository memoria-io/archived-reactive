package io.memoria.jutils.utils.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import static io.memoria.jutils.utils.netty.NettyClientUtils.get;
import static io.memoria.jutils.utils.netty.NettyServerUtils.statusReply;
import static io.memoria.jutils.utils.netty.NettyServerUtils.stringReply;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

public class NettyServerUtilsTest {
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
  public static void afterAll() {
    server.dispose();
  }

  @Test
  public void errorReplyTest() {
    var monoResp = get(host, errorReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(UNAUTHORIZED, "Unauthorized")).expectComplete().verify();
  }

  @Test
  public void statusReplyTest() {
    var monoResp = get(host, statusReplyPath);
    StepVerifier.create(monoResp)
                .expectNext(Tuple.of(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()))
                .expectComplete()
                .verify();
  }

  @Test
  public void stringReplyTest() {
    var monoResp = get(host, stringReplyPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, "Hello")).expectComplete().verify();
  }

  private static void routes(HttpServerRoutes routes) {
    routes.get(statusReplyPath, (req, resp) -> statusReply.apply(resp).apply(UNAUTHORIZED))
          .get(stringReplyPath, (req, resp) -> stringReply.apply(resp).apply(OK, "Hello"))
          .get(errorReplyPath, (req, resp) -> stringReply.apply(resp).apply(UNAUTHORIZED, UNAUTHORIZED.reasonPhrase()));
  }
}
