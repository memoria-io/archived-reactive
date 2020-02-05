package com.marmoush.jutils.eventsourcing.domain.value;

import java.time.LocalDateTime;

public class AbstractCommand extends AbstractEvent implements Command {

  public AbstractCommand(LocalDateTime creationTime) {
    super(creationTime);
  }
}
