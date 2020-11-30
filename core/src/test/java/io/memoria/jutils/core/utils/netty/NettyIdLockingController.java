package io.memoria.jutils.core.utils.netty;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;

public class NettyIdLockingController {
  private final Map<String, Integer> db = new HashMap<>();
  private final Map<String, Lock> locks = new HashMap<>();

  public Mono<Void> handle(HttpServerRequest req, HttpServerResponse resp) {
    var id = req.param("id");
    locks.putIfAbsent(id, new ReentrantLock());
    locks.get(id).lock();
    db.putIfAbsent(id, 0);
    var f = db.get(id);
    if (f != null) {
      db.put(id, f + 1);
    }
    locks.get(id).unlock();
    return resp.status(CREATED).sendString(Mono.just("" + f)).then();
  }
}
