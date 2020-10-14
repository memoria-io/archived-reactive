package io.memoria.jutils.adapter.yaml;

import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.adapter.transformer.yaml.YamlBeans;
import io.memoria.jutils.core.dto.DTO;
import io.memoria.jutils.core.transformer.yaml.Yaml;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.memoria.jutils.core.transformer.file.FileReader.resourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlBeansTest {
  private Yaml yaml = new YamlBeans();
  private String testYaml = Tests.FILE_READER.file(resourcePath("yaml/test.yaml").get()).block();

  @Test
  void deserializeYamlByClass() {
    var m = yaml.deserialize(testYaml, HashMap.class).get();
    assertEquals(m.get("stringValue"), "hello world");
  }

  @Test
  void deserializeYamlByType() {
    var hashMapType = new TypeToken<HashMap<String, Object>>() {}.getType();
    var m = yaml.<HashMap<String, Object>>deserialize(testYaml, hashMapType).get();
    assertEquals(m.get("stringValue"), "hello world");
  }

  @Test
  void deserializeYamlByDTO() {
    record EmployeeDTOO(String name) implements DTO<Employee> {
      @Override
      public Try<Employee> get() {
        return Try.success(new Employee(name));
      }
//      new E
    }

    var emp = yaml.deserializeByDTO("name: Bob", EmployeeDTOO.class).get();
    assertEquals(new Employee("Bob"), emp);
  }

  @Test
  void serializeObject() {

  }
}
