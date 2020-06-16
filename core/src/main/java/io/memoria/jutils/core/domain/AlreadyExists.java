package io.memoria.jutils.core.domain;

public class AlreadyExists extends Err {
  public static final AlreadyExists ALREADY_EXISTS = new AlreadyExists("Already exists");

  public AlreadyExists(String message) {
    super(message);
  }
}
