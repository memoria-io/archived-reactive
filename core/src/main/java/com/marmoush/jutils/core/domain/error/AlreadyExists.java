package com.marmoush.jutils.core.domain.error;

public class AlreadyExists extends Error {
  public static final AlreadyExists ALREADY_EXISTS = new AlreadyExists("Already exists");

  public AlreadyExists(String message) {
    super(message);
  }
}