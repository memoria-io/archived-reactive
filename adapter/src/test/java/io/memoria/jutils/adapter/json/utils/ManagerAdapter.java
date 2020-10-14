package io.memoria.jutils.adapter.json.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.memoria.jutils.adapter.transformer.json.JsonGsonUtils;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.io.IOException;

import static io.memoria.jutils.adapter.transformer.json.JsonGsonUtils.deserializeAsList;
import static io.memoria.jutils.core.transformer.json.JsonException.notFound;
import static io.memoria.jutils.core.transformer.json.JsonException.unknown;

public class ManagerAdapter extends TypeAdapter<Manager> {
  private final TypeAdapter<Engineer> engineerAdapter;

  public ManagerAdapter(TypeAdapter<Engineer> engineerAdapter) {
    this.engineerAdapter = engineerAdapter;
  }

  @Override
  public Manager read(JsonReader in) throws IOException {
    in.beginObject();
    Try<String> name = Try.failure(notFound("name"));
    List<Engineer> tasks = List.empty();
    while (in.hasNext()) {
      var nextName = in.nextName();
      switch (nextName) {
        case "name" -> name = Try.success(in.nextString());
        case "team" -> tasks = deserializeAsList(in, engineerAdapter).get();
        default -> throw unknown(nextName);
      }
    }
    in.endObject();
    return new Manager(name.get(), tasks);
  }

  @Override
  public void write(JsonWriter out, Manager eng) throws IOException {
    out.beginObject();
    out.name("name");
    out.value(eng.name());
    out.name("team");
    JsonGsonUtils.serialize(out, engineerAdapter, eng.team());
    out.endObject();
  }
}
