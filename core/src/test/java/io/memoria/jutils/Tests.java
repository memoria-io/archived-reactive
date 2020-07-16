package io.memoria.jutils;

import io.memoria.jutils.adapter.file.local.LocalFileReader;
import io.memoria.jutils.adapter.file.local.LocalFileWriter;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.file.FileWriter;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final FileWriter writer = new LocalFileWriter(Schedulers.boundedElastic());
  public static final FileReader reader = new LocalFileReader(Schedulers.boundedElastic());

  private Tests() {}
}
