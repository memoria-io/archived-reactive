package io.memoria.jutils.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.memoria.jutils.keyvaluestore.KeyValueStoreClient;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("UnstableApiUsage")
public record EtcdStoreClient(KV kvClient) implements KeyValueStoreClient {

  public EtcdStoreClient(Client client) {
    this(client.getKVClient());
  }

  public Mono<Void> delete(String key) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    return Mono.fromFuture(kvClient.delete(byteKey)).then();
  }

  public Mono<Option<String>> get(String key) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    return Mono.fromFuture(kvClient.get(byteKey)).map(GetResponse::getKvs).map(this::mapOf).map(c -> c.get(key));
  }

  public Mono<Map<String, String>> getAllWithPrefix(String key) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    return Mono.fromFuture(kvClient.get(byteKey, GetOption.newBuilder().withPrefix(byteKey).build()))
               .map(GetResponse::getKvs)
               .map(this::mapOf);
  }

  public Mono<Void> put(String key, String value) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    ByteSequence byteValue = ByteSequence.from(value.getBytes());
    return Mono.fromFuture(kvClient.put(byteKey, byteValue)).then();
  }

  private Map<String, String> mapOf(java.util.List<KeyValue> keyValues) {
    return HashMap.ofAll(keyValues.stream(), KeyValue::getKey, KeyValue::getValue)
                  .map((k, v) -> Tuple.of(k.toString(StandardCharsets.UTF_8), v.toString(StandardCharsets.UTF_8)));
  }
}