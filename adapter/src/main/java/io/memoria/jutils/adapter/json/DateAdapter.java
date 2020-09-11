package io.memoria.jutils.adapter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAdapter extends TypeAdapter<LocalDate> {
  private final DateTimeFormatter dateFormat;

  public DateAdapter(DateTimeFormatter dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    return LocalDate.parse(in.nextString(), dateFormat);
  }

  @Override
  public void write(JsonWriter out, LocalDate localDate) throws IOException {
    out.value(localDate.format(dateFormat));
  }
}
