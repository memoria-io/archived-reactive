package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.FileOps;
import io.memoria.reactive.core.stream.OMsg;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.util.function.Function.identity;

class TopicDirOps {
  public static final String FILE_EXT = ".json";
  public static final String INDEX_FILE_NAME = "index";

  private TopicDirOps() {}

  public static Mono<Path> createIndex(Path topicDir) {
    var idxFilePath = topicDir.resolve(INDEX_FILE_NAME);
    return Mono.fromCallable(() -> {
      if (!Files.exists(idxFilePath))
        return FileOps.write(idxFilePath, "-1");
      else
        return Mono.just(idxFilePath);
    }).flatMap(identity());
  }

  public static Mono<Integer> readIndex(Path topicDir) {
    return FileOps.read(topicDir.resolve(INDEX_FILE_NAME)).map(Integer::valueOf);
  }

  public static Flux<Path> stream(Path path) {
    return Flux.range(0, Integer.MAX_VALUE)
               .map(TopicDirOps::toFileName)
               .map(path::resolve)
               .takeWhile(Files::exists)
               .log("Stream")
               .concatWith(watch(path));
  }

  public static String toFileName(int key) {
    return String.format("%010d%s", key, FILE_EXT);
  }

  public static int toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(FILE_EXT, "");
    return Integer.parseInt(idxStr);
  }

  public static Mono<OMsg> toMsg(Path path) {
    var idx = toIndex(path);
    return FileOps.read(path).map(str -> new OMsg(idx, str));
  }

  public static Flux<Path> watch(Path dir) {
    return Mono.fromCallable(() -> createWatchService(dir))
               .flatMapMany(TopicDirOps::keepWatching)
               .map(dir::resolve)
               .log("watch");
  }

  public static Mono<Path> write(Path topicDirPath, int expectedIndex, String msg) {
    var fileName = toFileName(expectedIndex);
    var filePath = topicDirPath.resolve(fileName);
    var indexPath = topicDirPath.resolve(INDEX_FILE_NAME);
    return readIndex(topicDirPath).flatMap(i -> canWrite(i, expectedIndex))
                                  .then(FileOps.write(filePath, msg))
                                  .then(FileOps.rewrite(indexPath, String.valueOf(expectedIndex)))
                                  .thenReturn(filePath);
  }

  private static Mono<Integer> canWrite(int current, int expected) {
    if (current == expected - 1) {
      return Mono.just(expected);
    } else {
      var msg = "Expected index %d is not the next increment of index of %d".formatted(expected, current);
      return Mono.error(new IOException(msg));
    }
  }

  private static WatchService createWatchService(Path parentDir) throws IOException {
    var watchService = FileSystems.getDefault().newWatchService();
    parentDir.register(watchService, ENTRY_CREATE);
    return watchService;
  }

  private static Flux<String> keepWatching(WatchService ws) {
    return Flux.generate((SynchronousSink<List<String>> s) -> s.next(watchOnce(ws)))
               .concatMap(Flux::fromIterable)
               .log("KeepWatching");
  }

  private static List<String> watchOnce(WatchService ws) {
    try {
      WatchKey key = ws.take();
      var l = List.ofAll(key.pollEvents()).map(WatchEvent::context).map(Object::toString);
      key.reset();
      return l;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return List.empty();
    }
  }
}
