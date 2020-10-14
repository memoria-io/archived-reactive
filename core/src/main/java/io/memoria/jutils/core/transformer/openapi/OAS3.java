package io.memoria.jutils.core.transformer.openapi;

import io.memoria.jutils.core.regex.RegexPatterns;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.regex.Pattern;

public sealed interface OAS3 {
  record OASObject(String description, Map<String, OAS3> properties, List<String> required) implements OAS3 {
    @Override
    public String type() {
      return "object";
    }
  }

  record OASString(String format, Pattern pattern, int minLength, int maxLength) implements OAS3 {
    public static final OASString DATE = new OASString("date", RegexPatterns.RFC_3339_DATE);
    public static final OASString DATETIME = new OASString("date-time", RegexPatterns.RFC_3339_DATETIME);
    public static final OASString PASSWORD = new OASString("password", RegexPatterns.ALL);
    public static final OASString BYTE = new OASString("byte", RegexPatterns.BASE_64);
    public static final OASString BINARY = new OASString("binary", RegexPatterns.BASE_64);

    public OASString(String format, Pattern pattern) {
      this(format, pattern, 0, Integer.MAX_VALUE);
    }

    @Override
    public String type() {
      return "string";
    }
  }

  record OASArray(List<OAS3> items) implements OAS3 {
    @Override
    public String type() {
      return "array";
    }
  }

  record OASBoolean() implements OAS3 {
    @Override
    public String type() {
      return "boolean";
    }
  }

  record OASInteger() implements OAS3 {
    @Override
    public String type() {
      return "integer";
    }
  }

  record OASNumber() implements OAS3 {
    @Override
    public String type() {
      return "number";
    }
  }

  String type();
}
