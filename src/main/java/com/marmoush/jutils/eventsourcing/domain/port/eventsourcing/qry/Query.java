package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.qry;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class Query extends Event {
  public Query(String id) {
    super(id);
  }
}
