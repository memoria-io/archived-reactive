package com.marmoush.jutils.eventsourcing.domain.entity;

import java.time.LocalDateTime;

public class QueryRequest extends Event {
  public QueryRequest(String id, String flowId, LocalDateTime creationTime) {
    super(id, flowId, creationTime);
  }
}
