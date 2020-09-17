package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.state.State;

public class ESException extends Exception {
  public static class ESInvalidOperation extends ESException {
    private ESInvalidOperation(State state, Command command) {
      super("Invalid operation: %s on current state: %s".formatted(state.getClass().getName(),
                                                                   command.getClass().getName()));
    }
  }

  public static ESInvalidOperation invalidOperation(State state, Command command) {
    return new ESInvalidOperation(state, command);
  }

  public static ESException INVALID_AGGREGATE_ID = new ESException("Aggregate Id is null or empty");

  private ESException(String message) {
    super(message);
  }
}
