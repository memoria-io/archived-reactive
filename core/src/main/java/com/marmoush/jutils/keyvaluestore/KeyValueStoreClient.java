package com.marmoush.jutils.keyvaluestore;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface KeyValueStoreClient {
  Mono<Option<String>> get(String key);

  Mono<Map<String, String>> getAllWithPrefix(String key);

  Mono<String> put(String key, String value);

  Mono<Void> delete(String key);
}
