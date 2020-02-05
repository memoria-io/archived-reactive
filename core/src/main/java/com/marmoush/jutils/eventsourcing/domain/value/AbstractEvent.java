package com.marmoush.jutils.eventsourcing.domain.value;

import java.time.LocalDateTime;
import java.util.Objects;

public class AbstractEvent implements Event {
  public final LocalDateTime creationTime;

  public AbstractEvent(LocalDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    AbstractEvent that = (AbstractEvent) o;
    return creationTime.equals(that.creationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creationTime);
  }
}

