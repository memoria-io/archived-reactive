package io.memoria.jutils.core.domain.error;

public class NotFound extends Error {
  public static final NotFound NOT_FOUND = new NotFound("Not found");

  public NotFound(String message) {
    super(message);
  }
}
