package jackson.transformer;

import io.vavr.collection.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
