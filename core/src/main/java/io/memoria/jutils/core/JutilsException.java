package io.memoria.jutils.core;

public class JutilsException extends Exception {
  public static class AlreadyExists extends JutilsException {
    private AlreadyExists(String message) {
      super(message);
    }
  }

  public static class NotFound extends JutilsException {
    private NotFound(String message) {
      super(message);
    }
  }

  public static class ValidationError extends JutilsException {
    private ValidationError(String message) {
      super(message);
    }
  }

  public static AlreadyExists alreadyExists(String message) {
    return new AlreadyExists(message);
  }

  public static NotFound notFound(String message) {
    return new NotFound(message);
  }

  public static ValidationError validationError(String message) {
    return new ValidationError(message);
  }

  private JutilsException(String msg) {
    super(msg);
  }
}
