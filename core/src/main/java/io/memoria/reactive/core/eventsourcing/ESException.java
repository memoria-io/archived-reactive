package io.memoria.reactive.core.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class InvalidOperation extends ESException {
    public static InvalidOperation create(String state, String command) {
      return new InvalidOperation(state, command);
    }

    private InvalidOperation(String stateName, String commandName) {
      super("Invalid operation: %s on current state: %s".formatted(commandName, stateName));
    }
  }

  public static class UnknownCommand extends ESException {
    public static UnknownCommand create(String command) {
      return new UnknownCommand(command);
    }

    private UnknownCommand(String command) {
      super("No handler available for the command: %s ".formatted(command));
    }
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  private ESException(String message) {
    super(message);
  }
}
