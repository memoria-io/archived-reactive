package io.memoria.jutils.adapter.json.utils;

import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.adapter.json.JsonGson;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.memoria.jutils.core.json.Json;
import io.memoria.jutils.core.json.JsonException;
import io.vavr.collection.List;
import io.vavr.gson.VavrGson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.core.file.FileReader.resourcePath;

class JsonGsonUtilsTest {

  private static final Json j;
  private static final Engineer engineer;
  private static final Engineer otherEngineerObj;
  private static final Manager manager;
  private static final String managerJson;
  private static final String engineerJson;

  static {
    var engAdapter = new EngineerAdapter();
    var employeeAdapter = new EmployeeAdapter(engAdapter, new ManagerAdapter(engAdapter));
    j = new JsonGson(VavrGson.registerAll(new GsonBuilder().setPrettyPrinting()), employeeAdapter);
    otherEngineerObj = new Engineer("alex", List.of("fix issue 3", "Fix issue 4"));
    engineer = new Engineer("bob", List.of("fix issue 1", "Fix issue 2"));
    manager = new Manager("Annika", List.of(engineer, otherEngineerObj));
    managerJson = Tests.FILE_READER.file(resourcePath("json/Manager.json").get()).block();
    engineerJson = Tests.FILE_READER.file(resourcePath("json/Engineer.json").get()).block();
  }

  @Test
  void deserializeBadManagerDTO() {
    var managerTry = j.deserializeByDTO("{}", EmployeeDTO.class);
    Assertions.assertTrue(managerTry.isFailure());
    Assertions.assertTrue(managerTry.getCause() instanceof JsonException);
  }

  @Test
  void deserializeEmployeeDTO() {
    var engineer = j.deserializeByDTO(engineerJson, EmployeeDTO.class).get();
    var manager = j.deserializeByDTO(managerJson, EmployeeDTO.class).get();
    Assertions.assertEquals(JsonGsonUtilsTest.engineer, engineer);
    Assertions.assertEquals(JsonGsonUtilsTest.manager, manager);
  }

  @Test
  void deserializeEngineer() {
    var engineer = j.deserialize(engineerJson, Engineer.class).get();
    Assertions.assertEquals(JsonGsonUtilsTest.engineer, engineer);
  }

  @Test
  void deserializeManager() {
    var manager = j.deserialize(managerJson, Manager.class).get();
    Assertions.assertEquals(JsonGsonUtilsTest.manager, manager);
  }

  @Test
  void serializeEmployeeDTO() {
    Assertions.assertEquals(engineerJson, j.serialize(new EmployeeDTO(engineer)));
    Assertions.assertEquals(managerJson, j.serialize(new EmployeeDTO(manager)));
  }

  @Test
  void serializeEngineer() {
    Assertions.assertEquals(engineerJson, j.serialize(engineer));
  }

  @Test
  void serializeManager() {
    Assertions.assertEquals(managerJson, j.serialize(manager));
  }
}
