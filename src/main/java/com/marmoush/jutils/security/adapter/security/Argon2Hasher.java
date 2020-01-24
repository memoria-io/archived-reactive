package com.marmoush.jutils.security.adapter.security;

import com.marmoush.jutils.security.domain.port.Hasher;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2Hasher implements Hasher {
  private final Argon2 argon2;
  private final int iterations;
  private final int memory;
  private final int parallelism;

  public Argon2Hasher(int iterations, int memory, int parallelism) {
    argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    this.iterations = iterations;
    this.memory = memory;
    this.parallelism = parallelism;
  }

  @Override
  public String hash(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass);
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }

  @Override
  public boolean verify(String password, String hash, String salt) {
    boolean verify = argon2.verify(hash, password + salt);
    argon2.wipeArray(password.toCharArray());
    return verify;
  }
}
