package io.memoria.reactive.text.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.reactive.core.text.Json;
import io.memoria.reactive.core.text.TextException;
import io.vavr.API;
import io.vavr.Predicates;
import io.vavr.control.Try;

public record JsonJackson(ObjectMapper mapper) implements Json {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> blockingDeserialize(String str, Class<T> tClass) {
    return Try.of(() -> mapper.readValue(str, tClass))
              .mapFailure(API.Case(API.$(Predicates.instanceOf(JacksonException.class)),
                                   e -> new TextException(e.getMessage())));
  }

  @Override
  public <T> Try<String> blockingSerialize(T t) {
    return Try.of(() -> mapper.writeValueAsString(t));
  }
}
