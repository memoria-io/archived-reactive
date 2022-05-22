package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.vavr.control.Try;

public class Utils {
  private Utils() {}

  public static <E extends Event> Try<E> error(State state, Command command) {
    return Try.failure(InvalidOperation.create(state, command));
  }
}
