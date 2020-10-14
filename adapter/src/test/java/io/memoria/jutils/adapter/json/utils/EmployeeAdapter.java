package io.memoria.jutils.adapter.json.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.memoria.jutils.adapter.transformer.json.JsonGsonUtils;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.vavr.collection.HashMap;

import java.io.IOException;

class EmployeeAdapter extends TypeAdapter<Employee> {
  private final TypeAdapter<Engineer> engineerAdapter;
  private final TypeAdapter<Manager> managerAdapter;

  public EmployeeAdapter(TypeAdapter<Engineer> engineerAdapter, TypeAdapter<Manager> managerAdapter) {
    this.engineerAdapter = engineerAdapter;
    this.managerAdapter = managerAdapter;
  }

  @Override
  public Employee read(JsonReader in) throws IOException {
    return JsonGsonUtils.deserialize(in, HashMap.of("Engineer", engineerAdapter, "Manager", managerAdapter)).get();
  }

  @Override
  public void write(JsonWriter out, Employee p) throws IOException {
    out.beginObject();
    out.name(p.getClass().getSimpleName());
    if (p instanceof Engineer emp) {
      engineerAdapter.write(out, emp);
    }
    if (p instanceof Manager m) {
      managerAdapter.write(out, m);
    }
    out.endObject();
  }
}
