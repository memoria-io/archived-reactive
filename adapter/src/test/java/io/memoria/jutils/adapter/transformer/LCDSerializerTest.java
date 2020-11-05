package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.lcd.LCD.LCDArray;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDInteger;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDLong;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDMap;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDObject;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LCDSerializerTest {
  @Test
  void serializePrimitive() {
    // Given
    record TestPrimitives(String name, int age, long money) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestPrimitives.class);
    // Then
    assertTrue(oas3.properties().get("name") instanceof LCDString);
    assertTrue(oas3.properties().get("age") instanceof LCDInteger);
    assertTrue(oas3.properties().get("money") instanceof LCDLong);
  }

  @Test
  void serializeSubClass() {
    // Given
    class SomeClass {
      public final String name = "hello";
    }
    record TestSubClass(SomeClass someClass) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestSubClass.class);
    // Then
    assertTrue(oas3.properties().get("someClass") instanceof LCDObject sc &&
               sc.properties().get("name") instanceof LCDString);
  }

  @Test
  void serializeGenericSubClass() {
    // Given
    class Person<T> {}

    class SomeClass<A, B, C> {
      public A a;
      public B b;
      public C c;
      public final String name = "hello";
    }
    record TestGenericSubClass(SomeClass<Integer, String, Person<String>> someClass) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestGenericSubClass.class);
    // Then
    // Due to type erasure t is considered 
    assertTrue(oas3.properties().get("someClass") instanceof LCDObject);
    var obj = (LCDObject) oas3.properties().get("someClass");
    assertTrue(obj.properties().get("name") instanceof LCDString);
    assertTrue(obj.properties().get("a") instanceof LCDInteger);
  }

  @Test
  void serializeJavaCollection() {
    // Given
    record TestJavaCollection(ArrayList<String> list) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestJavaCollection.class);
    // Then
    assertTrue(oas3.properties().get("list") instanceof LCDArray list && list.type() instanceof LCDString);
  }

  @Test
  void serializeArray() {
    // Given
    record Person(String name) {}
    record TestArray(String[] names, int[] ages, Person[] persons) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestArray.class);
    // Then
    assertTrue(oas3.properties().get("names") instanceof LCDArray);
    assertTrue(oas3.properties().get("names") instanceof LCDArray arr && arr.type() instanceof LCDString);
    assertTrue(oas3.properties().get("ages") instanceof LCDArray arr && arr.type() instanceof LCDInteger);
    assertTrue(oas3.properties().get("persons") instanceof LCDArray arr && arr.type() instanceof LCDObject obj &&
               obj.properties().get("name") instanceof LCDString);
  }

  @Test
  void serializeMap() {
    // Given
    record TestMap(Map<String, Integer> map) {}
    // When
    var oas3 = (LCDObject) LCDSerializer.serialize(TestMap.class);
    // Then
    assertTrue(oas3.properties().get("map") instanceof LCDMap map && map.valuesType() instanceof LCDInteger);
  }
}

