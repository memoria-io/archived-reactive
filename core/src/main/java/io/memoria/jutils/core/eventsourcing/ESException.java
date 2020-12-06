package io.memoria.jutils.core.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class ESInvalidOperation extends ESException {
    private ESInvalidOperation(State state, Command command) {
      super("Invalid operation: %s on current state: %s".formatted(command.getClass().getSimpleName(),
                                                                   state.getClass().getSimpleName()));
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
