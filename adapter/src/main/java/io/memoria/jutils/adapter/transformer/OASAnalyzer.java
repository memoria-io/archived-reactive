package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.OAS3;
import io.memoria.jutils.core.transformer.OAS3.OAS3Array;
import io.memoria.jutils.core.transformer.OAS3.OAS3Boolean;
import io.memoria.jutils.core.transformer.OAS3.OAS3Double;
import io.memoria.jutils.core.transformer.OAS3.OAS3Float;
import io.memoria.jutils.core.transformer.OAS3.OAS3Integer;
import io.memoria.jutils.core.transformer.OAS3.OAS3Long;
import io.memoria.jutils.core.transformer.OAS3.OAS3Map;
import io.memoria.jutils.core.transformer.OAS3.OAS3Object;
import io.memoria.jutils.core.transformer.OAS3.OAS3String;
import io.vavr.control.Option;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class OASAnalyzer {
  public static <T> OAS3 analyse(Class<T> c) {
    return asPrimitive(c).orElse(() -> asStandardObject(c))
                         .orElse(() -> asArray(c))
                         .getOrElse(() -> asUserDefinedObject(c));

  }

  public static <T> OAS3 asUserDefinedObject(Class<T> c) {
    var map = new HashMap<String, OAS3>();
    var fields = (c.isRecord()) ? c.getDeclaredFields() : c.getFields();
    // TODO annotated public methods
    for (Field f : fields) {
      var oas = asJavaCollection(f).orElse(() -> asMap(f)).orElse(asGeneric(f)).getOrElse(analyse(f.getType()));
      map.put(f.getName(), oas);
    }
    return new OAS3Object(map);
  }

  public static <T> Option<OAS3> asGeneric(Field f) {
    if (f.getGenericType() instanceof ParameterizedType p) {
      return Option.some(fromGeneric(p, f.getType()));
    }
    return Option.none();
  }

  public static <T> OAS3 fromGeneric(ParameterizedType parameterizedType, Class<T> rawType) {
    // Get Parameter names in code
    var params = rawType.getTypeParameters();
    var paramsNames = Arrays.stream(params).map(TypeVariable::getName).collect(toSet());
    // Match types with names
    var nameAndType = new HashMap<String, Type>();
    var typeArguments = parameterizedType.getActualTypeArguments();
    for (int i = 0; i < typeArguments.length; i++) {
      nameAndType.put(params[i].getName(), typeArguments[i]);
    }
    var objectMap = new HashMap<String, OAS3>();
    var fields = (rawType.isRecord()) ? rawType.getDeclaredFields() : rawType.getFields();
    for (Field field : fields) {
      if (field.getType().equals(Object.class) && paramsNames.contains(field.getGenericType().getTypeName())) {
        var t = nameAndType.get(field.getGenericType().getTypeName());
        if (t instanceof Class<?> classT) {
          objectMap.put(field.getName(), analyse(classT));
        } else if (t instanceof ParameterizedType pt) {
          var oas = fromGeneric(pt, (Class<?>) pt.getRawType());
          objectMap.put(field.getName(), oas);
        }
      } else {
        objectMap.put(field.getName(), analyse(field.getType()));
      }
    }
    return new OAS3Object(objectMap);
  }

  public static Option<OAS3> asMap(Field f) {
    if (Map.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var valueClass = (Class<?>) type.getActualTypeArguments()[1];
      return Option.some(new OAS3Map(analyse(valueClass)));
    }
    return Option.none();
  }

  public static Option<OAS3> asJavaCollection(Field f) {
    if (Collection.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var cls = (Class<?>) type.getActualTypeArguments()[0];
      return Option.some(new OAS3Array(analyse(cls)));
    }
    return Option.none();
  }

  public static <T> Option<OAS3> asArray(Class<T> c) {
    if (c.isArray()) {
      var arrayType = c.getComponentType();
      var arrayOf = analyse(arrayType);
      return Option.some(new OAS3Array(arrayOf));
    }
    return Option.none();
  }

  public static <T> Option<OAS3> asStandardObject(Class<T> c) {
    if (c.equals(String.class))
      return Option.some(new OAS3String());
    if (c.equals(Boolean.class))
      return Option.some(new OAS3Boolean());
    if (c.equals(Character.class) || c.equals(Byte.class))
      return Option.some(new OAS3String());
    if (c.equals(Short.class) || c.equals(Integer.class))
      return Option.some(new OAS3Integer());
    if (c.equals(Long.class))
      return Option.some(new OAS3Long());
    if (c.equals(Float.class))
      return Option.some(new OAS3Float());
    if (c.equals(Double.class))
      return Option.some(new OAS3Double());
    return Option.none();
  }

  public static <T> Option<OAS3> asPrimitive(Class<T> c) {
    if (c.isPrimitive()) {
      if (c.equals(boolean.class))
        return Option.some(new OAS3Boolean());
      if (c.equals(char.class) || c.equals(byte.class))
        return Option.some(new OAS3String());
      if (c.equals(short.class) || c.equals(int.class))
        return Option.some(new OAS3Integer());
      if (c.equals(long.class))
        return Option.some(new OAS3Long());
      if (c.equals(float.class))
        return Option.some(new OAS3Float());
      if (c.equals(double.class))
        return Option.some(new OAS3Double());
    }
    return Option.none();
  }

  private OASAnalyzer() {}
}
