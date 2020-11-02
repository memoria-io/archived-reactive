package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.OAS3;
import io.memoria.jutils.core.transformer.SchemaTransformer;
import io.vavr.control.Try;

public record OASTransformer(SchemaTransformer<OAS3>... transformers) implements SchemaTransformer<OAS3> {

  @Override
  public <T> Try<Class<T>> deserialize(OAS3 OAS3, Class<T> c) {
    return null;
  }

  @Override
  public <T> Try<OAS3> serialize(Class<T> c) {

    return null;
  }
}
