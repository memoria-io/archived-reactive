package jackson.transformer;

import io.memoria.jutils.core.id.Id;
import io.vavr.collection.List;

public record Person(String name, List<Id> friendsIds) {}
