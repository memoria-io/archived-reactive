package io.memoria.reactive.eventsourcing.netty;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.memoria.reactive.eventsourcing.netty.TestUtils.httpClient;
import static io.memoria.reactive.eventsourcing.netty.TestUtils.httpServer;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

class HealthControllerTest {
  private static final AtomicBoolean flip = new AtomicBoolean(true);
  private static final String ERROR_MSG = "The flip was false!";
  private static final String endpoint = "/health";
  private static final HealthController healthCheck;
  private static final DisposableServer disposableServer;

  static {
    healthCheck = new HealthController(check());
    disposableServer = httpServer.route(routes()).bindNow();
  }

  @Test
  void failure() {
    // Given
    flip.set(false);
    var monoResp = NettyClientUtils.get(httpClient, endpoint);
    // Then
    StepVerifier.create(monoResp).expectNext(Response.of(INTERNAL_SERVER_ERROR, ERROR_MSG)).verifyComplete();
  }

  @Test
  void success() {
    // Given
    flip.set(true);
    var monoResp = NettyClientUtils.get(httpClient, endpoint);
    // Then
    StepVerifier.create(monoResp).expectNext(Response.of(OK, "ok")).verifyComplete();
  }

  @AfterAll
  static void afterAll() {
    disposableServer.dispose();
  }

  static Consumer<HttpServerRoutes> routes() {
    return r -> r.get(endpoint, healthCheck);
  }

  private static Mono<String> check() {
    return Mono.fromCallable(flip::get).flatMap(b -> (b) ? Mono.just("ok") : Mono.error(new Exception(ERROR_MSG)));
  }
}
