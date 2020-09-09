package io.memoria.jutils.adapter;

import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.file.LocalFileReader;
import io.memoria.jutils.adapter.file.LocalFileWriter;
import io.memoria.jutils.adapter.json.DurationAdapter;
import io.memoria.jutils.adapter.json.DateAdapter;
import io.memoria.jutils.adapter.json.DateTimeAdapter;
import io.memoria.jutils.adapter.json.TimeAdapter;
import io.memoria.jutils.adapter.json.PeriodAdapter;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.file.FileWriter;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Tests {
  public static final FileWriter FILE_WRITER = new LocalFileWriter(Schedulers.boundedElastic());
  public static final FileReader FILE_READER = new LocalFileReader(Schedulers.boundedElastic());

  public static GsonBuilder registerDate(GsonBuilder gsonBuilder, DateTimeFormatter dateFormatter) {
    return gsonBuilder.registerTypeAdapter(LocalDate.class, new DateAdapter(dateFormatter));
  }

  public static GsonBuilder registerDateTime(GsonBuilder gsonBuilder,
                                             DateTimeFormatter dateFormatter,
                                             ZoneOffset zoneOffset) {
    return gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter(dateFormatter, zoneOffset));
  }

  public static GsonBuilder registerDurationAdapter(GsonBuilder gsonBuilder) {
    return gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
  }

  public static GsonBuilder registerPeriodAdapter(GsonBuilder gsonBuilder) {
    return gsonBuilder.registerTypeAdapter(Period.class, new PeriodAdapter());
  }

  public static GsonBuilder registerTime(GsonBuilder gsonBuilder,
                                         DateTimeFormatter dateFormatter,
                                         ZoneOffset zoneOffset) {
    return gsonBuilder.registerTypeAdapter(LocalTime.class, new TimeAdapter(dateFormatter, zoneOffset));
  }

  private Tests() {}
}
