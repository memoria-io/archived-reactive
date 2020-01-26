package com.marmoush.jutils.eventsourcing.domain.entity;

import com.marmoush.jutils.general.domain.entity.Meta;

public class QueryRequest extends Event {
  public QueryRequest(Meta meta) {
    super(meta);
  }
}
