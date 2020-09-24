package io.memoria.jutils.adapter.json.utils;

import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.adapter.json.JsonGson;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.memoria.jutils.core.json.Json;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.core.file.FileReader.resourcePath;

class JsonGsonUtilsTest {

  private static final Json j;
  private static final Engineer engineerObj;
  private static final Engineer otherEngineerObj;
  private static final Manager managerObj;
  private static final String managerJson;
  private static final String engineerJson;

  static {
    var engAdapter = new EngineerAdapter();
    var employeeAdapter = new EmployeeAdapter(engAdapter, new ManagerAdapter(engAdapter));
    j = new JsonGson(new GsonBuilder().setPrettyPrinting(), employeeAdapter);
    otherEngineerObj = new Engineer("alex", List.of("fix issue 3", "Fix issue 4"));
    engineerObj = new Engineer("bob", List.of("fix issue 1", "Fix issue 2"));
    managerObj = new Manager("Annika", List.of(engineerObj, otherEngineerObj));
    managerJson = Tests.FILE_READER.file(resourcePath("json/Manager.json").get()).block();
    engineerJson = Tests.FILE_READER.file(resourcePath("json/Engineer.json").get()).block();
  }

  @Test
  void deserializeEngineer() {
    var manager = j.deserialize(engineerJson, Engineer.class).get();
    Assertions.assertThat(manager).isEqualTo(engineerObj);
  }

  @Test
  void deserializeManager() {
    var manager = j.deserialize(managerJson, Manager.class).get();
    Assertions.assertThat(manager).isEqualTo(managerObj);
  }

  @Test
  void serializeEngineer() {
    var bob = new Engineer("bob", List.of("fix issue 1", "Fix issue 2"));
    Assertions.assertThat(j.serialize(bob)).isEqualTo(engineerJson);
  }

  @Test
  void serializeManager() {
    Assertions.assertThat(j.serialize(managerObj)).isEqualTo(managerJson);
  }
}
