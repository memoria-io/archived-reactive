package io.memoria.jutils.core.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class UnknownCommand extends ESException {
    private UnknownCommand(Command command) {
      super("No handler available for the command: %s ".formatted(command.getClass().getSimpleName()));
    }
  }

  public static class InvalidOperation extends ESException {
    private InvalidOperation(State state, Command command) {
      super("Invalid operation: %s on current state: %s".formatted(command.getClass().getSimpleName(),
                                                                   state.getClass().getSimpleName()));
    }
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  public static InvalidOperation invalidOperation(State state, Command command) {
    return new InvalidOperation(state, command);
  }

  public static UnknownCommand unknownCommand(Command command) {
    return new UnknownCommand(command);
  }

  private ESException(String message) {
    super(message);
  }
}
