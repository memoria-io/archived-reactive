package com.marmoush.jutils.eventsourcing.domain.entity;

import com.marmoush.jutils.general.domain.entity.Meta;

public class QueryResponse extends Event {

  public QueryResponse(Meta meta) {
    super(meta);
  }
}
