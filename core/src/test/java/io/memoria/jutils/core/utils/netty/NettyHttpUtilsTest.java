package io.memoria.jutils.core.utils.netty;

import io.vavr.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import java.util.Base64;
import java.util.function.Consumer;

import static io.memoria.jutils.core.utils.netty.NettyHttpUtils.basicFrom;
import static io.memoria.jutils.core.utils.netty.NettyHttpUtils.send;
import static io.memoria.jutils.core.utils.netty.NettyHttpUtils.sendError;
import static io.memoria.jutils.core.utils.netty.NettyHttpUtils.tokenFrom;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vavr.control.Option.some;

public class NettyHttpUtilsTest {
  private static final String happyPath = "/happy";
  private static final String sadPath = "/sad";
  // basic and token only separated for simplicity, in production it should be one path
  private static final String basicPath = "/authenticate_basic";
  private static final String tokenPath = "/authenticate_token";
  private static final HttpClient client = HttpClient.create().baseUrl("127.0.0.1:8081");
  private static final DisposableServer server = HttpServer.create()
                                                           .host("127.0.0.1")
                                                           .port(8081)
                                                           .route(routes())
                                                           .bindNow();

  @AfterAll
  public static void afterAll() {
    server.dispose();
  }

  private static Consumer<HttpServerRoutes> routes() {
    return r -> r.get(happyPath, (req, resp) -> send(resp, OK))
                 .get(sadPath, (req, resp) -> sendError(resp, new NettyHttpError(BAD_REQUEST)))
                 .get(tokenPath, (req, resp) -> send(resp, OK, some(tokenFrom(req).get())))
                 .get(basicPath, (req, resp) -> send(resp, OK, some(basicFrom(req).get().toString())));
  }

  @Test
  public void sendBasicAuthTest() {
    var cred = Base64.getEncoder().encodeToString(("bob:password").getBytes());
    var monoResp = client.headers(b -> b.add("Authorization", "Basic " + cred))
                         .get()
                         .uri(basicPath)
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp).expectNext(Tuple.of(200, "(bob, password)")).expectComplete().verify();
  }

  @Test
  public void sendErrorTest() {
    var monoResp = client.get()
                         .uri(sadPath)
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp)
                .expectNext(Tuple.of(BAD_REQUEST.code(), BAD_REQUEST.reasonPhrase()))
                .expectComplete()
                .verify();
  }

  @Test
  public void sendTest() {
    var monoResp = client.get()
                         .uri(happyPath)
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp).expectNext(Tuple.of(OK.code(), OK.reasonPhrase())).expectComplete().verify();
  }

  @Test
  public void sendTokenAuthTest() {
    var token = "xyz.xyz.xyz";
    var monoResp = client.headers(b -> b.add("Authorization", "Bearer " + token))
                         .get()
                         .uri(tokenPath)
                         .responseSingle((res, body) -> Mono.just(res.status().code()).zipWith(body.asString()))
                         .map(t -> Tuple.of(t.getT1(), t.getT2()));
    StepVerifier.create(monoResp).expectNext(Tuple.of(200, token)).expectComplete().verify();
  }
}
