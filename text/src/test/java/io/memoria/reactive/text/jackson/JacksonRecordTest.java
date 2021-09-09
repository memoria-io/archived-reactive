package io.memoria.reactive.text.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.memoria.reactive.text.jackson.cases.company.CompanyData;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.memoria.reactive.text.jackson.TestDeps.json;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A Copy from https://github.com/FasterXML/jackson-databind/blob/977fbb3dcfa7ad7fda4f45f7f0c1afa0916b702e/src/test/java/com/fasterxml/jackson/databind/BaseMapTest.java
 */
class JacksonRecordTest {

  record JsonIgnoreRecord(int id, @JsonIgnore String name) {}

  record RecordOfRecord(SimpleRecord record) {}

  record RecordWithConstructor(int id, String name) {
    public RecordWithConstructor(int id) {
      this(id, "name");
    }
  }

  record SimpleRecord(int id, String name) {}

  private static JsonMapper jsonMapper;

  public String asYaml(String jsonString) throws IOException {
    // parse JSON
    JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
    // save it as YAML
    return new YAMLMapper().writeValueAsString(jsonNodeTree);
  }

  @Test
  void jsonToYaml() {

  }

  @BeforeAll
  static void setUp() {
    jsonMapper = new JsonMapper();
  }

  @Test
  void testDeserializeRecordWithConstructor() throws IOException {
    RecordWithConstructor value = jsonMapper.readValue("{\"id\":123,\"name\":\"Bob\"}", RecordWithConstructor.class);

    assertEquals(new RecordWithConstructor(123, "Bob"), value);
  }

  @Test
  void testDeserializeSimpleRecord() throws IOException {
    SimpleRecord value = jsonMapper.readValue("{\"id\":123,\"name\":\"Bob\"}", SimpleRecord.class);

    assertEquals(new SimpleRecord(123, "Bob"), value);
  }

  @Test
  void testSerializeJsonIgnoreRecord() throws JsonProcessingException {
    JsonIgnoreRecord record = new JsonIgnoreRecord(123, "Bob");

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"id\":123}", json);
  }

  @Test
  void testSerializeRecordOfRecord() throws JsonProcessingException {
    RecordOfRecord record = new RecordOfRecord(new SimpleRecord(123, "Bob"));

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"record\":{\"id\":123,\"name\":\"Bob\"}}", json);
  }

  @Test
  void testSerializeSimpleRecord() throws JsonProcessingException {
    SimpleRecord record = new SimpleRecord(123, "Bob");

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"id\":123,\"name\":\"Bob\"}", json);
  }

  @Test
  void toList() {
    // When
    var list = json.deserialize(CompanyData.JSON_LIST, String[].class).block();
    // Then
    assert list != null;
    assertEquals(List.of("mercedes", "chevy", "porsche"), List.of(list));
  }
}
