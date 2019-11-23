package com.marmoush.jutils.security;

import java.util.Random;

public class RandomUtilsImpl implements RandomUtils {
  public static final int DEFAULT_SALT_SIZE = 32;
  public static final int DEFAULT_PASS_MIN_SIZE = 128;
  public static final int DEFAULT_PASS_MAX_SIZE = 256;

  private final int passMin;
  private final int passMax;
  private final int saltSize;
  private Random random;

  public RandomUtilsImpl(Random random) {
    this.random = random;
    this.saltSize = DEFAULT_SALT_SIZE;
    this.passMin = DEFAULT_PASS_MIN_SIZE;
    this.passMax = DEFAULT_PASS_MAX_SIZE;
  }

  public RandomUtilsImpl(Random random, int passMin, int passMax, int saltSize) {
    this.random = random;
    this.passMin = passMin;
    this.passMax = passMax;
    this.saltSize = saltSize;
  }

  @Override
  public String randomHex(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    return sb.substring(0, length);
  }

  @Override
  public String randomPassword() {
    int length = random.nextInt(passMax - passMin) + passMin;
    return randomHex(length);
  }

  @Override
  public String randomSalt() {
    return randomHex(saltSize);
  }
}
