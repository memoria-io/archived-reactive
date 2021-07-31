package io.memoria.reactive.core.text;

import io.vavr.control.Try;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class SerializableTransformer implements TextTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> {
      var b64 = Base64.getDecoder().decode(str);
      var is = new ByteArrayInputStream(b64);
      try (var in = new ObjectInputStream(is)) {
        return (T) in.readObject();
      }
    });
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> {
      var os = new ByteArrayOutputStream();
      try (var out = new ObjectOutputStream(os)) {
        out.writeObject(t);
      }
      return Base64.getEncoder().encodeToString(os.toByteArray());
    });
  }
}