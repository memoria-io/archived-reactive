package io.memoria.jutils.adapter.json.utils;

import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.adapter.json.JsonGson;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.memoria.jutils.adapter.json.utils.EmployeeDTO.EngineerDTO;
import io.memoria.jutils.adapter.json.utils.EmployeeDTO.ManagerDTO;
import io.memoria.jutils.core.json.Json;
import io.vavr.collection.List;
import io.vavr.gson.VavrGson;
import org.junit.jupiter.api.Assertions;
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
    j = new JsonGson(VavrGson.registerAll(new GsonBuilder().setPrettyPrinting()), employeeAdapter);
    otherEngineerObj = new Engineer("alex", List.of("fix issue 3", "Fix issue 4"));
    engineerObj = new Engineer("bob", List.of("fix issue 1", "Fix issue 2"));
    managerObj = new Manager("Annika", List.of(engineerObj, otherEngineerObj));
    managerJson = Tests.FILE_READER.file(resourcePath("json/Manager.json").get()).block();
    engineerJson = Tests.FILE_READER.file(resourcePath("json/Engineer.json").get()).block();
  }

  @Test
  void deserializeBadManagerDTO() {
    var managerTry = j.deserializeByDTO("{}", EmployeeDTO.class);
    Assertions.assertTrue(managerTry.isFailure());
    System.out.println(managerTry.getCause().toString());
  }

  @Test
  void deserializeEngineer() {
    var engineer = j.deserialize(engineerJson, Engineer.class).get();
    Assertions.assertEquals(engineerObj, engineer);
  }

  @Test
  void deserializeEngineerDTO() {
    var engineer = j.deserializeByDTO(engineerJson, EmployeeDTO.class).get();
    Assertions.assertEquals(engineerObj, engineer);
  }

  @Test
  void deserializeManager() {
    var manager = j.deserialize(managerJson, Manager.class).get();
    Assertions.assertEquals(managerObj, manager);
  }

  @Test
  void deserializeManagerDTO() {
    var manager = j.deserializeByDTO(managerJson, EmployeeDTO.class).get();
    Assertions.assertEquals(managerObj, manager);
  }

  @Test
  void serializeEngineer() {
    Assertions.assertEquals(engineerJson, j.serialize(engineerObj));
  }

  @Test
  void serializeEngineerDTO() {
    var dto = new EmployeeDTO(engineerObj);
    Assertions.assertEquals(engineerJson, j.serialize(dto));
  }

  @Test
  void serializeManager() {
    Assertions.assertEquals(managerJson, j.serialize(managerObj));
  }

  @Test
  void serializeManagerDTO() {
    var dto = new EmployeeDTO(managerObj);
    Assertions.assertEquals(managerJson, j.serialize(dto));
  }
}
