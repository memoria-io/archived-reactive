package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.OAS3.OAS3Array;
import io.memoria.jutils.core.transformer.OAS3.OAS3Integer;
import io.memoria.jutils.core.transformer.OAS3.OAS3Long;
import io.memoria.jutils.core.transformer.OAS3.OAS3Map;
import io.memoria.jutils.core.transformer.OAS3.OAS3Object;
import io.memoria.jutils.core.transformer.OAS3.OAS3String;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OASAnalyzerTest {
  @Test
  void analysePrimitive() {
    // Given
    record TestPrimitives(String name, int age, long money) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestPrimitives.class);
    // Then
    assertTrue(oas3.properties().get("name") instanceof OAS3String);
    assertTrue(oas3.properties().get("age") instanceof OAS3Integer);
    assertTrue(oas3.properties().get("money") instanceof OAS3Long);
  }

  @Test
  void analyseSubClass() {
    // Given
    class SomeClass {
      public final String name = "hello";
    }
    record TestSubClass(SomeClass someClass) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestSubClass.class);
    // Then
    assertTrue(oas3.properties().get("someClass") instanceof OAS3Object sc &&
               sc.properties().get("name") instanceof OAS3String);
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
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestGenericSubClass.class);
    // Then
    // Due to type erasure t is considered 
    assertTrue(oas3.properties().get("someClass") instanceof OAS3Object);
    var obj = (OAS3Object) oas3.properties().get("someClass");
    assertTrue(obj.properties().get("name") instanceof OAS3String);
    assertTrue(obj.properties().get("a") instanceof OAS3Integer);
  }

  @Test
  void analyseJavaCollection() {
    // Given
    record TestJavaCollection(ArrayList<String> list) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestJavaCollection.class);
    // Then
    assertTrue(oas3.properties().get("list") instanceof OAS3Array list && list.type() instanceof OAS3String);
  }

  @Test
  void analyseArray() {
    // Given
    record Person(String name) {}
    record TestArray(String[] names, int[] ages, Person[] persons) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestArray.class);
    // Then
    assertTrue(oas3.properties().get("names") instanceof OAS3Array);
    assertTrue(oas3.properties().get("names") instanceof OAS3Array arr && arr.type() instanceof OAS3String);
    assertTrue(oas3.properties().get("ages") instanceof OAS3Array arr && arr.type() instanceof OAS3Integer);
    assertTrue(oas3.properties().get("persons") instanceof OAS3Array arr && arr.type() instanceof OAS3Object obj &&
               obj.properties().get("name") instanceof OAS3String);
  }

  @Test
  void analyseMap() {
    // Given
    record TestMap(Map<String, Integer> map) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(TestMap.class);
    // Then
    assertTrue(oas3.properties().get("map") instanceof OAS3Map map && map.valuesType() instanceof OAS3Integer);
  }
}

