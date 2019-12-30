package com.marmoush.jutils.domain.value;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
  public final String key;
  public final String value;
  public final LocalDateTime creationDate;

  public Event(String key, String value) {
    this.key = key;
    this.value = value;
    this.creationDate = LocalDateTime.now();
  }

  public Event(String key, String value, LocalDateTime creationDate) {
    this.key = key;
    this.value = value;
    this.creationDate = creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Event event = (Event) o;
    return key.equals(event.key) && value.equals(event.value) && creationDate.equals(event.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, creationDate);
  }
}
