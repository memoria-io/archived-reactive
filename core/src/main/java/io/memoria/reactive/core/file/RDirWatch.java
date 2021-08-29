package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.util.function.Function.identity;

class RDirWatch {

  public static Flux<Path> watch(Path path) {
    return Mono.fromCallable(() -> watchService(path)).flatMapMany(RDirWatch::watchService).map(path::resolve);
  }

  private RDirWatch() {}

  private static Flux<String> take(WatchService watchService) {
    return Mono.fromCallable(() -> {
      WatchKey key = watchService.take();
      var l = List.ofAll(key.pollEvents()).map(WatchEvent::context).map(Object::toString);
      key.reset();
      return l;
    }).flatMapMany(Flux::fromIterable);
  }

  private static Flux<String> watchService(WatchService ws) {
    return Flux.generate((SynchronousSink<Flux<String>> s) -> s.next(take(ws))).concatMap(identity());
  }

  private static WatchService watchService(Path dir) throws IOException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    dir.register(watchService, ENTRY_CREATE);
    return watchService;
  }
}
