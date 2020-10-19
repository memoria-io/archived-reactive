package io.memoria.jutils.jackson.transformer.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.jutils.core.transformer.yaml.Yaml;
import io.vavr.control.Try;

public record YamlJackson(ObjectMapper objMapper) implements Yaml {

  @Override
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> objMapper.readValue(str, tClass));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> objMapper.writeValueAsString(t));
  }
}
