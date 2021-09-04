package io.memoria.reactive.core.id;

import java.util.concurrent.atomic.AtomicLong;

public record FileIdGenerator(String prefix, AtomicLong atomicLong) implements IdGenerator {
  public static final String SEP = "_";

  public static FileIdGenerator startFrom(String fileName) {
    var components = fileName.split(SEP);
    var prefix = components[0];
    var counter = Long.parseLong(components[1]);
    return new FileIdGenerator(prefix, counter);
  }

  public FileIdGenerator(String prefix, long counter) {
    this(prefix, new AtomicLong(counter));
  }

  @Override
  public Id get() {
    return Id.of(prefix + SEP + atomicLong.getAndIncrement());
  }
}
