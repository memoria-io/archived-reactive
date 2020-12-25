package io.memoria.jutils.jconf;

import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.memoria.jutils.jackson.transformer.JacksonUtils;
import io.memoria.jutils.jackson.transformer.yaml.YamlJackson;
import io.vavr.collection.Map;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

public class JConf {
  public static final String DEFAULT_FILE_INCLUSION_MARKER = "include:";

  private final String inclusionMarker;
  private final FileUtils fileUtils;
  private final StringTransformer transformer;
  private final Map<String, String> configStore;

  public static JConf build(Map<String, String> values) {
    var fileUtils = FileUtils.build();
    var yom = JacksonUtils.defaultYaml();
    JacksonUtils.mixinPropertyFormat(yom);
    var transformer = new YamlJackson(yom);
    return new JConf(DEFAULT_FILE_INCLUSION_MARKER, fileUtils, transformer, values);
  }

  public static JConf build(String inclusionMarker,
                            FileUtils fileUtils,
                            StringTransformer transformer,
                            Map<String, String> values) {
    return new JConf(inclusionMarker, fileUtils, transformer, values);
  }

  private JConf(String inclusionMarker,
                FileUtils fileUtils,
                StringTransformer transformer,
                Map<String, String> configStore) {
    this.inclusionMarker = inclusionMarker;
    this.fileUtils = fileUtils;
    this.transformer = transformer;
    this.configStore = configStore;
  }

  public <T> Mono<T> read(Path path, Class<T> as) {
    var lines = fileUtils.readLines(path, inclusionMarker);
    var docMono = JConfUtils.resolveLines(lines, this.configStore);
    return docMono.flatMap(str -> toMono(transformer.deserialize(str, as)));
  }

  public <T> Mono<T> readResource(String path, Class<T> as) {
    var lines = fileUtils.readResourceLines(path, inclusionMarker);
    var docMono = JConfUtils.resolveLines(lines, this.configStore);
    return docMono.flatMap(str -> toMono(transformer.deserialize(str, as)));
  }
}
