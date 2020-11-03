package io.memoria.jutils.jackson.transformer.oas;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OAS3Dto {
  OAS3Type type();

  record OAS3StringDto(Optional<OAS3StringFormat> format) implements OAS3Dto {
    @Override
    public OAS3Type type() {
      return OAS3Type.STRING;
    }
  }

  record OAS3ObjectDto(Map<String, OAS3Dto> properties, List<String> required) implements OAS3Dto {
    @Override
    public OAS3Type type() {
      return OAS3Type.OBJECT;
    }
  }
}
