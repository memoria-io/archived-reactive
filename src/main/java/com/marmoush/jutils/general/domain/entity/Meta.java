package com.marmoush.jutils.general.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Meta {
  public final String id;
  public final String flowId;
  public final LocalDateTime creationTime;

  public Meta(String id, String flowId) {
    this(id, flowId, LocalDateTime.now());
  }

  public Meta(String id, String flowId, LocalDateTime creationTime) {
    this.id = id;
    this.flowId = flowId;
    this.creationTime = creationTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Meta event = (Meta) o;
    return id.equals(event.id) && flowId.equals(event.flowId) && creationTime.equals(event.creationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, flowId, creationTime);
  }
}
