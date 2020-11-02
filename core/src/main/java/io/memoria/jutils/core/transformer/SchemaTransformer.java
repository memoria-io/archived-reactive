package io.memoria.jutils.core.transformer;

import io.vavr.control.Try;

public interface SchemaTransformer<E> {
  <T> Try<Class<T>> deserialize(E e, Class<T> c);

  <T> Try<E> serialize(Class<T> c);
}
