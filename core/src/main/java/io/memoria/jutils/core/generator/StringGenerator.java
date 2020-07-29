package io.memoria.jutils.core.generator;

public interface StringGenerator {
  String alphanumeric(int length);

  String hex(int length);

  String minMaxAlphanumeric(int min, int max);

  String minMaxHex(int min, int max);
}
