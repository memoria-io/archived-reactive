package io.memoria.jutils.adapter.id;

import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.core.id.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(atomicLong.getAndIncrement() + "");
  }
}
