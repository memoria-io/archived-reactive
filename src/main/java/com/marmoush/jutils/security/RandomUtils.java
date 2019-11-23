package com.marmoush.jutils.security;

public interface RandomUtils {
  String randomHex(int length);

  String randomPassword();

  String randomSalt();
}
