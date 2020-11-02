package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.OAS3.OAS3Array;
import io.memoria.jutils.core.transformer.OAS3.OAS3Integer;
import io.memoria.jutils.core.transformer.OAS3.OAS3Long;
import io.memoria.jutils.core.transformer.OAS3.OAS3Object;
import io.memoria.jutils.core.transformer.OAS3.OAS3String;
import net.jodah.typetools.TypeResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OASAnalyzerTest {
  @Test
  void fromGenericTest() {
    Map<String, Integer> map = new HashMap<>();
    List<String> l = new ArrayList<>();

    Type typeArgs = TypeResolver.reify(l.getClass());

    ParameterizedType paramType = (ParameterizedType) typeArgs;
    Type[] actualTypeArgs = paramType.getActualTypeArguments();
    ParameterizedType arg = (ParameterizedType) actualTypeArgs[0];

    System.out.println(arg);
    //    System.out.println(typeArgs.getTypeName());
  }

  @Test
  void analysePrimitiveRecord() {
    record Hello(String name, int age, long money) {}
    var oas3 = (OAS3Object) OASAnalyzer.analyse(Hello.class);
    assertTrue(oas3.properties().get("name") instanceof OAS3String);
    assertTrue(oas3.properties().get("age") instanceof OAS3Integer);
    assertTrue(oas3.properties().get("money") instanceof OAS3Long);
  }

  @Test
  void analyseArray() {
    // Given
    class SomeClass {
      public final String name = "hello";
    }
    record Person(String name) {}
    record Hello(String name,
                 String[] names,
                 int[] ages,
                 Person[] persons,
                 ArrayList<String> list,
                 SomeClass someClass) {}
    // When
    var oas3 = (OAS3Object) OASAnalyzer.analyse(Hello.class);
    // Then
    assertTrue(oas3.properties().get("name") instanceof OAS3String);
    assertTrue(oas3.properties().get("names") instanceof OAS3Array);
    assertTrue(oas3.properties().get("names") instanceof OAS3Array arr && arr.type() instanceof OAS3String);
    assertTrue(oas3.properties().get("ages") instanceof OAS3Array arr && arr.type() instanceof OAS3Integer);
    assertTrue(oas3.properties().get("persons") instanceof OAS3Array arr && arr.type() instanceof OAS3Object obj &&
               obj.properties().get("name") instanceof OAS3String);
    assertTrue(oas3.properties().get("list") instanceof OAS3Object list && list.properties().size() == 0);
    assertTrue(oas3.properties().get("someClass") instanceof OAS3Object sc && sc.properties().size() == 1);
  }

  @Test
  void isClassTest() {
    System.out.println(OASAnalyzer.isClass(int.class));
  }

  @Test
  void javaCollectionTest() {
    Collection.class.isAssignableFrom(List.class);
  }
}

