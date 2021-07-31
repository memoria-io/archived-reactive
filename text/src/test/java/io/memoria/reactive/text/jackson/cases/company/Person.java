package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

public record Person(String name, List<Id> friendsIds) {}
