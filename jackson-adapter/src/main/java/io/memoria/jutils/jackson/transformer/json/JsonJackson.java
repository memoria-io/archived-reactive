package io.memoria.jutils.jackson.transformer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.jutils.core.transformer.json.Json;
import io.vavr.control.Try;

public record JsonJackson(ObjectMapper mapper) implements Json {

  @Override
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> mapper.readValue(str, tClass));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> mapper.writeValueAsString(t));
  }
}
