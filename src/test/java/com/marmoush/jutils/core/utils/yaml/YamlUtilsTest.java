package com.marmoush.jutils.core.utils.yaml;

import com.marmoush.jutils.core.utils.yaml.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlUtilsTest {
  @Test
  public void parseYamlShouldReturnList() {
    YamlConfigMap map = YamlUtils.parseYamlResource("test.yaml").get();
    List<String> list = map.asStringList("list");

    assertEquals(list, List.of("hi", "hello", "bye"));
    assertEquals("hello world", map.asString("sub.config.name"));

    assertEquals("byebye", map.asString("sub.other.value"));
    List<String> subList = map.asStringList("sub.list");
    assertEquals(subList, List.of("hi", "hello", "bye"));
  }
}
