package io.memoria.jutils.jtext.jackson.cases.company;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;

public record Person(String name, List<Id> friendsIds) {}
