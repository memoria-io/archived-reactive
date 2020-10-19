package io.memoria.jutils.etcd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.etcd.jetcd.Client;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.memoria.jutils.jackson.transformer.yaml.YamlJackson;
import io.vavr.jackson.datatype.VavrModule;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Random;

import static io.vavr.API.Some;
import static io.vavr.control.Option.none;

class EtcdIT {
  private static record YamlConfig(String url) {}

  private static final YamlConfig yamlConfig;
  private final Client clientBuilt;
  private final EtcdStoreClient client;
  private final String keyPrefix = "myKey";
  private final String value = "myValue";

  static {
    // File
    var files = new FileUtils(Schedulers.boundedElastic());
    var etcdString = files.readResource("etcd.yaml").block();
    // Yaml
    ObjectMapper jacksonMapper = new ObjectMapper(new YAMLFactory());
    jacksonMapper.registerModule(new VavrModule());
    var yaml = new YamlJackson(jacksonMapper);
    yamlConfig = yaml.deserialize(etcdString, YamlConfig.class).get();
  }

  EtcdIT() {
    clientBuilt = Client.builder().endpoints(yamlConfig.url).build();
    client = new EtcdStoreClient(clientBuilt);
  }

  @Test
  void deletionTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.delete(key)).expectComplete().verify();
    StepVerifier.create(client.get(key)).expectNext(none()).expectComplete().verify();
  }

  @Test
  void getMapTest() {
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
  void getTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.get(key)).expectNext(Some(value)).expectComplete().verify();
  }

  @Test
  void putTest() {
    String key = keyPrefix + new Random().nextInt(1000);
    StepVerifier.create(client.put(key, value)).expectComplete().verify();
    StepVerifier.create(client.put(key, "new_value")).expectComplete().verify();
  }
}
