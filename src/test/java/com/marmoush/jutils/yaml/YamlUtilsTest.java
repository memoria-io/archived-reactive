package com.marmoush.jutils.yaml;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlUtilsTest {
  @Test
  public void parseYamlShouldReturnList() throws IOException {
    Map<String, Object> map = YamlUtils.parseYaml("test.yaml").get();
    @SuppressWarnings("unchecked")
    List<String> list = (ArrayList<String>) map.get("list");

    assertEquals(list, List.of("hi", "hello", "bye"));
    assertEquals("hello world", map.get("sub.config.name"));
    assertEquals("byebye", map.get("sub.other.value"));
    @SuppressWarnings("unchecked")
    List<String> subList = (List<String>) map.get("sub.list");
    assertEquals(subList, List.of("hi", "hello", "bye"));
  }
}
