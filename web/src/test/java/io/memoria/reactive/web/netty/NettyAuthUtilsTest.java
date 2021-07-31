package io.memoria.reactive.web.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import java.util.Base64;
import java.util.function.Consumer;

import static io.memoria.reactive.web.netty.NettyClientUtils.get;
import static io.memoria.reactive.web.netty.NettyClientUtils.post;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

class NettyAuthUtilsTest {
  // basic and token only separated for simplicity, in production it should be one path
  private static final String basicAuthPath = "/authenticate_basic";
  private static final String tokenAuthPath = "/authenticate_token";
  private static final String host = "127.0.0.1:8081";
  private static final DisposableServer server = HttpServer.create()
                                                           .host("127.0.0.1")
                                                           .port(8081)
                                                           .route(routes())
                                                           .bindNow();

  @AfterAll
  static void afterAll() {
    server.dispose();
  }

  @Test
  @DisplayName("Should deserialize Basic authorization header correctly")
  void basicFromTest() {
    var basic = Base64.getEncoder().encodeToString(("bob:password").getBytes());
    var monoResp = NettyClientUtils.post("payload hello", b -> b.add("Authorization", "Basic " + basic), host, basicAuthPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, "(bob, password)")).expectComplete().verify();
  }

  @Test
  @DisplayName("Should deserialize bearer authorization header correctly")
  void tokenFromTest() {
    var token = "xyz.xyz.xyz";
    var monoResp = NettyClientUtils.get(b -> b.add("Authorization", "Bearer " + token), host, tokenAuthPath);
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK, token)).expectComplete().verify();
  }

  private static Consumer<HttpServerRoutes> routes() {
    return r -> r.get(tokenAuthPath, (req, resp) -> NettyServerUtils.stringReply.apply(resp).apply(OK, NettyAuthUtils.bearerToken(req).get()))
                 .post(basicAuthPath,
                       (req, resp) -> NettyServerUtils.stringReply.apply(resp).apply(OK, NettyAuthUtils.basicCredentials(req).get().toString()));
  }
}
