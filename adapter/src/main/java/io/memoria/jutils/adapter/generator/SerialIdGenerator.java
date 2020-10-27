package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  @Override
  public Id get() {
    return new Id(atomicLong.getAndIncrement() + "");
  }
}
