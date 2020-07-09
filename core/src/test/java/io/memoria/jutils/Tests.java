package io.memoria.jutils;

import io.memoria.jutils.core.utils.file.DefaultReactiveFileReader;
import io.memoria.jutils.core.utils.file.DefaultReactiveFileWriter;
import io.memoria.jutils.core.utils.file.ReactiveFileReader;
import io.memoria.jutils.core.utils.file.ReactiveFileWriter;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final ReactiveFileWriter writer = new DefaultReactiveFileWriter(Schedulers.boundedElastic());
  public static final ReactiveFileReader reader = new DefaultReactiveFileReader(Schedulers.boundedElastic());

  private Tests() {}
}
