package io.memoria.jutils.etcd;

import io.etcd.jetcd.Client;
import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.Random;

import static io.vavr.API.Some;
import static io.vavr.control.Option.none;

public class EtcdIT {
  private final YamlConfigMap config;
  private final Client clientBuilt;
  private final EtcdStoreClient client;
  private final String keyPrefix = "myKey";
  private final String value = "myValue";

  public EtcdIT() {
    config = Objects.requireNonNull(YamlUtils.parseYamlResource("etcd.yaml",false).block());
    clientBuilt = Client.builder().endpoints(config.asString("etcd.url").get()).build();
    client = new EtcdStoreClient(clientBuilt);
  }

  @Test
  public void deletionTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.delete(key)).expectComplete().verify();
    StepVerifier.create(client.get(key)).expectNext(none()).expectComplete().verify();
  }

  @Test
  public void getMapTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key + "0", value)).expectComplete().verify();
    StepVerifier.create(client.put(key + "1", value)).expectComplete().verify();
    StepVerifier.create(client.put(key + "2", value)).expectComplete().verify();
    StepVerifier.create(client.put(key + "3", value)).expectComplete().verify();
    StepVerifier.create(client.getAllWithPrefix(key))
                .expectNextMatches(m -> m.get(key + "0").get().equals(value) && m.get(key + "1").get().equals(value) &&
                                        m.get(key + "2").get().equals(value) && m.get(key + "3").get().equals(value));
  }

  @Test
  public void getTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.get(key)).expectNext(Some(value)).expectComplete().verify();
  }

  @Test
  public void putTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.put(key, "new_value")).expectComplete().verify();
  }
}
