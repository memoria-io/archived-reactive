package io.memoria.reactive.core.id;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(atomicLong.getAndIncrement() + "");
  }
}
