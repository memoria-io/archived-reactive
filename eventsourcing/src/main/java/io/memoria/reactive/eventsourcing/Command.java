package io.memoria.reactive.eventsourcing;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  CommandId commandId();

  long timestamp();
}
