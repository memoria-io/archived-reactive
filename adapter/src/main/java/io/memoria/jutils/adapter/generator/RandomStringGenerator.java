package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.StringGenerator;

import java.util.Random;

public record RandomStringGenerator(Random random) implements StringGenerator {
  public static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public String alphanumeric(int length) {
    StringBuilder sb = new StringBuilder(length);
    while (sb.length() < length)
      sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
    return sb.toString();
  }

  public String hex(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    return sb.substring(0, length);
  }

  public String minMaxAlphanumeric(int min, int max) {
    return alphanumeric(random.nextInt(max - min) + min);
  }

  public String minMaxHex(int min, int max) {
    return hex(random.nextInt(max - min) + min);
  }
}
