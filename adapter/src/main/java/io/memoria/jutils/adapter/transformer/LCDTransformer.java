package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.lcd.LCD;
import io.memoria.jutils.core.transformer.SchemaTransformer;
import io.vavr.control.Try;

public record LCDTransformer(SchemaTransformer<LCD>... transformers) implements SchemaTransformer<LCD> {

  @Override
  public <T> Try<Class<T>> deserialize(LCD LCD, Class<T> c) {
    return null;
  }

  @Override
  public <T> Try<LCD> serialize(Class<T> c) {

    return null;
  }
}
