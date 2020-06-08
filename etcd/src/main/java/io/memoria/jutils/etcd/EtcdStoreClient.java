package io.memoria.jutils.etcd;

import io.memoria.jutils.keyvaluestore.KeyValueStoreClient;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class EtcdStoreClient implements KeyValueStoreClient {
  private final KV kvClient;

  public EtcdStoreClient(Client client) {
    this.kvClient = client.getKVClient();
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

  public Mono<String> put(String key, String value) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    ByteSequence byteValue = ByteSequence.from(value.getBytes());
    return Mono.fromFuture(kvClient.put(byteKey, byteValue, PutOption.newBuilder().withPrevKV().build()))
               .map(c -> c.getPrevKv().getValue().toString(StandardCharsets.UTF_8) + "");
  }

  public Mono<Void> delete(String key) {
    ByteSequence byteKey = ByteSequence.from(key.getBytes());
    return Mono.fromFuture(kvClient.delete(byteKey)).then();
  }

  private Map<String, String> mapOf(java.util.List<KeyValue> keyValues) {
    return HashMap.ofAll(keyValues.stream(), KeyValue::getKey, KeyValue::getValue)
                  .map((k, v) -> Tuple.of(k.toString(StandardCharsets.UTF_8), v.toString(StandardCharsets.UTF_8)));
  }
}
