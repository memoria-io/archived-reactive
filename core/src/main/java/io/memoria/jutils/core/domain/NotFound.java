package io.memoria.jutils.core.domain;

public class NotFound extends Err {
  public static final NotFound NOT_FOUND = new NotFound("Not found");

  public NotFound(String message) {
    super(message);
  }
}
