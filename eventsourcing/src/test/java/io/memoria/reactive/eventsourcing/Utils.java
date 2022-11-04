package io.memoria.reactive.eventsourcing;

import io.memoria.reactive.eventsourcing.ESException.InvalidOperation;
import io.vavr.control.Try;

public class Utils {
  private Utils() {}

  public static <E extends Event> Try<E> error(State state, Command command) {
    return Try.failure(InvalidOperation.create(state, command));
  }
}
