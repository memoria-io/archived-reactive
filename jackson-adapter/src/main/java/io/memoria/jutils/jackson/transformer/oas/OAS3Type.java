package io.memoria.jutils.jackson.transformer.oas;

public enum OAS3Type {
  OBJECT("object"),
  STRING("string"),
  NUMBER("number"),
  INTEGER("integer"),
  BOOLEAN("boolean"),
  ARRAY("array");
  
  public final String name;

  OAS3Type(String name) {
    this.name = name;
  }
}
