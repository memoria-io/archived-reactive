package io.memoria.jutils.jes.socialnetwork;

public class SNException extends Exception {
  public static SNException alreadyExists(String message) {
    return new SNException(message);
  }

  public static SNException notFound(String message) {
    return new SNException(message);
  }

  private SNException(String message) {
    super(message);
  }
}
