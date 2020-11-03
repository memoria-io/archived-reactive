package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.oas.Pojo.PojoArray;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoInteger;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoLong;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoMap;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoObject;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PojoAnalyzerTest {
  @Test
  void analysePrimitive() {
    // Given
    record TestPrimitives(String name, int age, long money) {}
    // When
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestPrimitives.class);
    // Then
    assertTrue(oas3.properties().get("name") instanceof PojoString);
    assertTrue(oas3.properties().get("age") instanceof PojoInteger);
    assertTrue(oas3.properties().get("money") instanceof PojoLong);
  }

  @Test
  void analyseSubClass() {
    // Given
    class SomeClass {
      public final String name = "hello";
    }
    record TestSubClass(SomeClass someClass) {}
    // When
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestSubClass.class);
    // Then
    assertTrue(oas3.properties().get("someClass") instanceof PojoObject sc &&
               sc.properties().get("name") instanceof PojoString);
  }

  @Test
  void analyseGenericSubClass() {
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
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestGenericSubClass.class);
    // Then
    // Due to type erasure t is considered 
    assertTrue(oas3.properties().get("someClass") instanceof PojoObject);
    var obj = (PojoObject) oas3.properties().get("someClass");
    assertTrue(obj.properties().get("name") instanceof PojoString);
    assertTrue(obj.properties().get("a") instanceof PojoInteger);
  }

  @Test
  void analyseJavaCollection() {
    // Given
    record TestJavaCollection(ArrayList<String> list) {}
    // When
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestJavaCollection.class);
    // Then
    assertTrue(oas3.properties().get("list") instanceof PojoArray list && list.type() instanceof PojoString);
  }

  @Test
  void analyseArray() {
    // Given
    record Person(String name) {}
    record TestArray(String[] names, int[] ages, Person[] persons) {}
    // When
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestArray.class);
    // Then
    assertTrue(oas3.properties().get("names") instanceof PojoArray);
    assertTrue(oas3.properties().get("names") instanceof PojoArray arr && arr.type() instanceof PojoString);
    assertTrue(oas3.properties().get("ages") instanceof PojoArray arr && arr.type() instanceof PojoInteger);
    assertTrue(oas3.properties().get("persons") instanceof PojoArray arr && arr.type() instanceof PojoObject obj &&
               obj.properties().get("name") instanceof PojoString);
  }

  @Test
  void analyseMap() {
    // Given
    record TestMap(Map<String, Integer> map) {}
    // When
    var oas3 = (PojoObject) OASAnalyzer.analyse(TestMap.class);
    // Then
    assertTrue(oas3.properties().get("map") instanceof PojoMap map && map.valuesType() instanceof PojoInteger);
  }
}

