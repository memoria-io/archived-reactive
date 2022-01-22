package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public record FileStreamRepo(Path rootPath) implements OStreamRepo {

  @Override
  public Mono<Void> create(String topic) {
    var topicPath = this.rootPath.resolve(topic);
    return TopicDirOps.createIndex(topicPath).then();
  }

  @Override
  public Mono<Integer> size(String topic) {
    var topicPath = this.rootPath.resolve(topic);
    return TopicDirOps.readIndex(topicPath);
  }

  @Override
  public Mono<OMsg> publish(String topic, OMsg msg) {
    var topicPath = this.rootPath.resolve(topic);
    return TopicDirOps.write(topicPath, msg.sKey(), msg.value()).thenReturn(msg);
  }

  @Override
  public Flux<OMsg> subscribe(String topic, int skipped) {
    var topicPath = this.rootPath.resolve(topic);
    return TopicDirOps.stream(topicPath).skip(skipped).concatMap(TopicDirOps::toMsg);
  }
}
