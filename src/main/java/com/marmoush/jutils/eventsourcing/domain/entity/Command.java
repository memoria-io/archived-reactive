package com.marmoush.jutils.eventsourcing.domain.entity;

import com.marmoush.jutils.general.domain.entity.Meta;

public class Command extends Event {
  public Command(Meta meta) {
    super(meta);
  }
}
