package io.memoria.jutils.adapter;

import io.memoria.jutils.adapter.file.LocalFileReader;
import io.memoria.jutils.adapter.file.LocalFileWriter;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.file.FileWriter;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final FileWriter FILE_WRITER = new LocalFileWriter(Schedulers.boundedElastic());
  public static final FileReader FILE_READER = new LocalFileReader(Schedulers.boundedElastic());

  private Tests() {}
}
