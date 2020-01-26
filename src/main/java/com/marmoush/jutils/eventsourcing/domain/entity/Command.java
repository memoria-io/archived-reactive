package com.marmoush.jutils.eventsourcing.domain.entity;

import java.time.LocalDateTime;

public class Command extends Event {
  public Command(String id, String flowId, LocalDateTime creationTime) {
    super(id, flowId, creationTime);
  }
}
