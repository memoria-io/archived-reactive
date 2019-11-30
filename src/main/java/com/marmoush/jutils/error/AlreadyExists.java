package com.marmoush.jutils.error;

public class AlreadyExists extends Error {
  public static AlreadyExists ALREADY_EXISTS = new AlreadyExists("Already exists");

  public AlreadyExists(String message) {
    super(message);
  }
}
