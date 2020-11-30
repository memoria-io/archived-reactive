package io.memoria.jutils.core.utils.netty;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.memoria.jutils.core.utils.netty.NettyClientUtils.get;
import static java.util.Objects.requireNonNull;

class NettyIdLockingTest {

  private static final String path = "/{id}";
  private static final String host = "127.0.0.1:8082";
  private static final DisposableServer server = HttpServer.create()
                                                           .host("127.0.0.1")
                                                           .port(8082)
                                                           .route(NettyIdLockingTest::routes)
                                                           .bindNow();

  @AfterAll
  static void afterAll() {
    server.dispose();
  }

  @Test
  void stringReplyTest() throws InterruptedException {
    var es = Executors.newFixedThreadPool(10);
    for (int i = 0; i < 100; i++) {
      es.submit(() -> {
        get(host, "/first").subscribe();
        get(host, "/second").subscribe();
        get(host, "/third").subscribe();
      });
    }
    es.awaitTermination(2, TimeUnit.SECONDS);
    var r1 = requireNonNull(get(host, "/first").block())._2;
    var r2 = requireNonNull(get(host, "/second").block())._2;
    var r3 = requireNonNull(get(host, "/third").block())._2;
    Assertions.assertEquals("100", r1);
    Assertions.assertEquals("100", r2);
    Assertions.assertEquals("100", r3);
  }

  private static void routes(HttpServerRoutes routes) {
    var controller = new NettyIdLockingController();
    routes.get(path, controller::handle);
  }
}
