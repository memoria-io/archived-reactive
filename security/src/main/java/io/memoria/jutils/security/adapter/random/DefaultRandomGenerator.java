package io.memoria.jutils.security.adapter.random;

import io.memoria.jutils.security.RandomGenerator;

import java.util.Random;

public record DefaultRandomGenerator(Random random) implements RandomGenerator {
  public static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public String randomAlphanumeric(int length) {
    StringBuilder sb = new StringBuilder(length);
    while (sb.length() < length)
      sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
    return sb.toString();
  }

  public String randomHex(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    return sb.substring(0, length);
  }

  public String randomMinMaxAlphanumeric(int min, int max) {
    return randomAlphanumeric(random.nextInt(max - min) + min);
  }

  public String randomMinMaxHex(int min, int max) {
    return randomHex(random.nextInt(max - min) + min);
  }
}
