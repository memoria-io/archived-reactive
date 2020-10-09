package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.state.State;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class ESInvalidOperation extends ESException {
    private ESInvalidOperation(State state, Command command) {
      super("Invalid operation: %s on current state: %s".formatted(state.getClass().getSimpleName(),
                                                                   command.getClass().getSimpleName()));
    }
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  public static ESInvalidOperation invalidOperation(State state, Command command) {
    return new ESInvalidOperation(state, command);
  }

  private ESException(String message) {
    super(message);
  }
}
