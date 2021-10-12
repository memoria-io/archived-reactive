package io.memoria.reactive.core.resource;

import io.vavr.control.Try;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Resources {
  public static Try<String> read(String path) {
    return Try.of(() -> resource(path));
  }

  private Resources() {}

  private static String resource(String path) throws IOException {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
      return new String(Objects.requireNonNull(is).readAllBytes());
    }
  }
}
