package io.memoria.jutils.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class InvalidOperation extends ESException {
    private InvalidOperation(String stateName, String commandName) {
      super("Invalid operation: %s on current state: %s".formatted(commandName, stateName));
    }
  }

  public static class UnknownCommand extends ESException {
    private UnknownCommand(String command) {
      super("No handler available for the command: %s ".formatted(command));
    }
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  public static InvalidOperation invalidOperation(String state, String command) {
    return new InvalidOperation(state, command);
  }

  public static UnknownCommand unknownCommand(String command) {
    return new UnknownCommand(command);
  }

  private ESException(String message) {
    super(message);
  }
}
