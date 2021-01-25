package io.memoria.jutils.jtext;

import io.memoria.jutils.core.id.Id;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(atomicLong.getAndIncrement() + "");
  }
}
