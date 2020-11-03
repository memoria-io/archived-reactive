package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.oas.Pojo;
import io.memoria.jutils.core.transformer.SchemaTransformer;
import io.vavr.control.Try;

public record OASTransformer(SchemaTransformer<Pojo>... transformers) implements SchemaTransformer<Pojo> {

  @Override
  public <T> Try<Class<T>> deserialize(Pojo Pojo, Class<T> c) {
    return null;
  }

  @Override
  public <T> Try<Pojo> serialize(Class<T> c) {

    return null;
  }
}
