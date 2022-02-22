package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceFileOps {
  private ResourceFileOps() {}

  public static Try<String> read(String path) {
    return Try.of(() -> resource(path));
  }

  public static List<String> readResourceOrFile(String path) {
    try {
      if (path.startsWith("/")) {
        return List.ofAll(Files.lines(Path.of(path)).toList());
      } else {
        try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
          return List.ofAll(new BufferedReader(new InputStreamReader(inputStream)).lines());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String resource(String path) throws IOException {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
      return new String(Objects.requireNonNull(is).readAllBytes());
    }
  }
}
