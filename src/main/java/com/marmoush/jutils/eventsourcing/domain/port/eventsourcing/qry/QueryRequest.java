package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.qry;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class QueryRequest extends Event {
  public QueryRequest(String id) {
    super(id);
  }
}
