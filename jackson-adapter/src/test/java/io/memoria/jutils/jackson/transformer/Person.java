package io.memoria.jutils.jackson.transformer;

import io.memoria.jutils.core.value.Id;
import io.vavr.collection.List;

public record Person(String name, List<Id> friendsIds) {}
