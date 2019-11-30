package com.marmoush.jutils.error;

public class NotFound extends Error {
  public static NotFound NOT_FOUND = new NotFound("Not found");

  public NotFound(String message) {
    super(message);
  }
}
