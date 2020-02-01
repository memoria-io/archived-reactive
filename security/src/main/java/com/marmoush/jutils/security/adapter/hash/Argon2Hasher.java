package com.marmoush.jutils.security.adapter.hash;

import com.marmoush.jutils.security.domain.port.Hasher;
import de.mkammerer.argon2.*;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;

public class Argon2Hasher implements Hasher {
  private final Argon2 argon2;
  private final int iterations;
  private final int memory;
  private final int parallelism;
  private final Scheduler scheduler;

  public Argon2Hasher(int iterations, int memory, int parallelism, Scheduler scheduler) {
    argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    this.iterations = iterations;
    this.memory = memory;
    this.parallelism = parallelism;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<String> hash(String password, String salt) {
    return Mono.defer(() -> Mono.just(hashPassword(password, salt)).subscribeOn(scheduler));
  }

  @Override
  public Mono<Boolean> verify(String password, String hash, String salt) {
    return Mono.defer(() -> Mono.just(verifyPassword(password, hash, salt)).subscribeOn(scheduler));
  }

  private String hashPassword(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass);
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }

  private boolean verifyPassword(String password, String hash, String salt) {
    boolean verify = argon2.verify(hash, password + salt);
    argon2.wipeArray(password.toCharArray());
    return verify;
  }
}
