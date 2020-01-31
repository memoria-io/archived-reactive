package com.marmoush.jutils.security.domain.port;

public interface Hasher {
  String hash(String password, String salt);

  boolean verify(String password, String hash, String salt);
}
