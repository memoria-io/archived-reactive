package com.marmoush.jutils.eventsourcing.domain.entity;

import com.marmoush.jutils.general.domain.entity.Meta;

import java.util.Objects;

public class Event {
  public final Meta meta;

  public Event(Meta meta) {
    this.meta = meta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Event event = (Event) o;
    return meta.equals(event.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(meta);
  }
}
