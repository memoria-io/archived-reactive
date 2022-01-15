package io.memoria.reactive.core.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  private ESException(String message) {
    super(message);
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  public static class InvalidOperation extends ESException {
    private InvalidOperation(String stateName, String commandName) {
      super("Invalid operation: %s on current state: %s".formatted(commandName, stateName));
    }

    public static InvalidOperation create(State state, Command command) {
      return new InvalidOperation(state.getClass().getSimpleName(), command.getClass().getSimpleName());
    }

    public static InvalidOperation create(String state, String command) {
      return new InvalidOperation(state, command);
    }
  }

  public static class UnknownCommand extends ESException {
    private UnknownCommand(String command) {
      super("No handler available for the command: %s ".formatted(command));
    }

    public static UnknownCommand create(Command command) {
      return new UnknownCommand(command.getClass().getSimpleName());
    }

    public static UnknownCommand create(String command) {
      return new UnknownCommand(command);
    }
  }
}
