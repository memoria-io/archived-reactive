package io.memoria.reactive.core.db;

import java.io.Serializable;

public interface Msg extends Comparable<Msg>, Serializable {
  default int compareTo(Msg o) {
    return Long.compare(this.id(), o.id());
  }

  long id();
}
