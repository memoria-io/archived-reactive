package io.memoria.jutils.keyvaluestore;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface KeyValueStoreClient {
  Mono<Void> delete(String key);

  Mono<Option<String>> get(String key);

  Mono<Map<String, String>> getAllWithPrefix(String key);

  Mono<Void> put(String key, String value);
}
