package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Version;

import java.time.LocalDateTime;

public record Meta(LocalDateTime createdAt, Version version) {
  public Meta() {
    this(LocalDateTime.now(), new Version());
  }

  public Meta withCreatedAt(LocalDateTime createdAt) {
    return new Meta(createdAt, this.version);
  }

  public Meta withVersion(Version version) {
    return new Meta(this.createdAt, version);
  }
}
