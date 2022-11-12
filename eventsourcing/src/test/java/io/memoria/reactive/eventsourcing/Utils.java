package io.memoria.reactive.eventsourcing;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.exception.DeciderException.InvalidCommand;
import io.vavr.control.Try;

public class Utils {
  private Utils() {}

  public static <E extends Event> Try<E> error(State state, Command command) {
    return Try.failure(InvalidCommand.create(state, command));
  }
}
