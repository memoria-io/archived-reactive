package com.marmoush.jutils.utils.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.test.StepVerifier;

public class NettyHttpUtilsTest {
  private static NettyHttpError error = new NettyHttpError(new Exception("test error"), 400);
  private static DisposableServer server;
  private static HttpClient client;

  @BeforeAll
  public static void beforeAll() {
    server = HttpServer.create()
                       .host("127.0.0.1")
                       .port(8081)
                       .route(r -> r.get("/happy", (req, resp) -> NettyHttpUtils.send(resp, 200, "hello"))
                                    .get("/sad", (req, resp) -> NettyHttpUtils.sendError(resp, error)))
                       .bindNow();
    client = HttpClient.create().baseUrl("127.0.0.1:8081");
  }

  @Test
  public void sendTest() {
    var monoResp = client.get()
                         .uri("/happy")
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp).expectNext(Tuple.of(200, "hello")).expectComplete().verify();
  }

  @Test
  public void sendErrorTest() {
    var monoResp = client.get()
                         .uri("/sad")
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp).expectNext(Tuple.of(400, "test error")).expectComplete().verify();
  }

  @AfterAll
  public static void afterAll() {
    server.dispose();
  }
}
