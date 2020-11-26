package io.memoria.jutils.core.value;

import io.vavr.control.Try;

public record Version(int major, int minor, int patch) {
  public static Try<Version> from(String version) {
    return Try.of(() -> {
      var v = version.split("\\.");
      return new Version(Integer.parseInt(v[0]), Integer.parseInt(v[1]), Integer.parseInt(v[2]));
    });
  }

  public Version() {
    this(0, 0, 0);
  }
}
