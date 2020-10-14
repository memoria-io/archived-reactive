package io.memoria.jutils.core.transformer;

import io.memoria.jutils.core.dto.DTO;
import io.vavr.control.Try;

import java.lang.reflect.Type;

public interface StringTransformer {
  <T> Try<T> deserialize(String str, Type type);

  <T> Try<T> deserialize(String str, Class<T> tClass);

  <T> Try<T> deserializeByDTO(String str, Class<? extends DTO<T>> tClass);

  <T> Try<String> serialize(T t);
}
