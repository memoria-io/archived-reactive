package com.marmoush.jutils.eventsourcing.domain.entity;

import java.time.LocalDateTime;

public class QueryResponse extends Event {

  public QueryResponse(String id, String flowId, LocalDateTime creationTime) {
    super(id, flowId, creationTime);
  }
}
