package io.memoria.jutils.security.adapter.random;

import java.util.Objects;
import java.util.Random;

public class RandomUtils {
  public static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private final Random random;

  public RandomUtils(Random random) {
    this.random = random;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    RandomUtils that = (RandomUtils) o;
    return random.equals(that.random);
  }

  @Override
  public int hashCode() {
    return Objects.hash(random);
  }
}
