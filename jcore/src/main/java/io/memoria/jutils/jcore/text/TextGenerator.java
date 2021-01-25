package io.memoria.jutils.jcore.text;

import java.util.Random;

public final class TextGenerator {
  public static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private final Random random;

  public TextGenerator(Random random) {
    this.random = random;
  }

  public String alphanumeric(int length) {
    StringBuilder sb = new StringBuilder(length);
    while (sb.length() < length)
      sb.append(ALPHANUMERICS.charAt(random.nextInt(ALPHANUMERICS.length())));
    return sb.toString();
  }

  public String hex(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    return sb.substring(0, length);
  }

  public String minMaxAlphanumeric(int minLength, int maxLength) {
    return alphanumeric(random.nextInt(maxLength - minLength) + minLength);
  }

  public String minMaxHex(int minLength, int maxLength) {
    return hex(random.nextInt(maxLength - minLength) + minLength);
  }
}
