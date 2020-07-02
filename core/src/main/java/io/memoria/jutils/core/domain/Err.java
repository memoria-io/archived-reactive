package io.memoria.jutils.core.domain;

public class Err extends Exception {
  public static class AlreadyExists extends Err {
    public static final AlreadyExists ALREADY_EXISTS = new AlreadyExists("Already exists");

    public AlreadyExists(String message) {
      super(message);
    }
  }

  public static class NotFound extends Err {
    public static final NotFound NOT_FOUND = new NotFound("Not found");

    public NotFound(String message) {
      super(message);
    }
  }

  public Err(String msg) {
    super(msg);
  }
}
