package io.memoria.reactive.text.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.reactive.core.text.TextException;
import io.memoria.reactive.core.text.Yaml;
import reactor.core.publisher.Mono;

public record YamlJackson(ObjectMapper mapper) implements Yaml {

  @Override
  public <T> Mono<T> deserialize(String str, Class<T> tClass) {
    return Mono.fromCallable(() -> mapper.readValue(str, tClass))
               .onErrorMap(JacksonException.class, e -> new TextException(e.getMessage()));
  }

  @Override
  public <T> Mono<String> serialize(T t) {
    return Mono.fromCallable(() -> mapper.writeValueAsString(t).trim());
  }
}
