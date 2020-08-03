package io.memoria.jutils.core;

public class JutilsException extends Exception {
  public static class AlreadyExists extends JutilsException {
    public static final AlreadyExists ALREADY_EXISTS = new AlreadyExists("Already exists");

    public AlreadyExists(String message) {
      super(message);
    }
  }

  public static class NotFound extends JutilsException {
    public static final NotFound NOT_FOUND = new NotFound("Not found");

    public NotFound(String message) {
      super(message);
    }
  }

  public JutilsException(String msg) {
    super(msg);
  }
}
