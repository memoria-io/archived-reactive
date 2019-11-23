package com.marmoush.jutils.security;

public interface Hasher {
  String hash(String password, String salt);

  boolean verify(String password, String hash, String salt);
}
