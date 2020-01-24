package com.marmoush.jutils.general.domain.port;

public interface Hasher {
  String hash(String password, String salt);

  boolean verify(String password, String hash, String salt);
}
