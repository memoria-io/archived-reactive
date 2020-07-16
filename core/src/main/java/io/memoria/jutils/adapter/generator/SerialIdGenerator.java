package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  @Override
  public String get() {
    return atomicLong.getAndIncrement() + "";
  }
}
