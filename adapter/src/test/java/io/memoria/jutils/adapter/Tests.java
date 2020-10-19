package io.memoria.jutils.adapter;

import io.memoria.jutils.core.utils.file.FileUtils;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final FileUtils files;

  static {
    // File utils
    files = new FileUtils(Schedulers.elastic());

  }

  private Tests() {}
}
