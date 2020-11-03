package io.memoria.jutils.core.transformer.oas;

public interface OasType {
  record AnyOf(OasOneType... values) implements OasType {}

  record OneOf(OasOneType... values) implements OasType {}

  enum OasOneType implements OasType {
    OBJECT("object"),
    STRING("string"),
    NUMBER("number"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    ARRAY("array");

    public final String name;

    OasOneType(String name) {
      this.name = name;
    }
  }
}
