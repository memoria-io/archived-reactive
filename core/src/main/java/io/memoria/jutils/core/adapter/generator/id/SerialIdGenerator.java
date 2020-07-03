package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SerialIdGenerator implements IdGenerator {
  private final AtomicLong atomicLong;

  public SerialIdGenerator(AtomicLong atomicLong) {
    this.atomicLong = atomicLong;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    SerialIdGenerator that = (SerialIdGenerator) o;
    return atomicLong.get() == that.atomicLong.get();
  }

  @Override
  public String get() {
    return atomicLong.getAndIncrement() + "";
  }

  @Override
  public int hashCode() {
    return Objects.hash(atomicLong);
  }
}
