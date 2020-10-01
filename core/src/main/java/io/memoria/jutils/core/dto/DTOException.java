package io.memoria.jutils.core.dto;

public class DTOException extends Exception {
  public static final DTOException NULL_PROPERTIES = new DTOException("All properties were null");

  public DTOException(String message) {
    super(message);
  }
}
