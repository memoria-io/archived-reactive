package io.memoria.jutils.core.generator;

public interface RandomGenerator {
  String randomAlphanumeric(int length);

  String randomHex(int length);

  String randomMinMaxAlphanumeric(int min, int max);

  String randomMinMaxHex(int min, int max);
}