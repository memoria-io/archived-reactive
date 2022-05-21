[![Release](https://github.com/memoria-io/reactive/workflows/Release/badge.svg)](https://github.com/memoria-io/reactive/actions?query=workflow%3ARelease)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/memoria-io/reactive?label=Version&logo=github)](https://github.com/orgs/memoria-io/packages?repo_name=reactive)
[![GitHub commits since latest release (by date)](https://img.shields.io/github/commits-since/memoria-io/reactive/latest?logoColor=github)](https://github.com/memoria-io/reactive/commits/master)

[![codecov](https://codecov.io/gh/memoria-io/reactive/branch/master/graph/badge.svg?token=hR4YugU12n)](https://codecov.io/gh/memoria-io/reactive)

# reactive

> هذا العلم والعمل وقف للّه تعالي اسأل اللّه ان يرزقنا الاخلاص فالقول والعمل
>
> This work and knowledge is for the sake of Allah, may Allah grant us sincerity in what we say or do.

`reactive` is a tool-box that makes it easier and faster to build reactive applications. It relies heavily
on [Reactive Streams](https://www.reactive-streams.org/) and uses [Project-Reactor](https://projectreactor.io/),
[Reactor Netty](https://github.com/reactor/reactor-netty), it also uses functional paradigms and collections
from [Vavr](https://www.vavr.io/).

## JDK notes

* reactive is up-to-date with the latest versions of JDK preview feature, currently `Java 18`.

## Core Features

* Vavr + Reactor Functional utilities
* Reactor Netty utility functions
* Reactive CQRS and Eventsourcing utilities (beta stage)
* Reactive functional in-memory generic cruds (for tests, not for production)
* Jackson Adapter (Json & Yaml) utilities
* FileOps reader utility
* ResourceFileOps utility
* ConfigFileOps is a module for reading yaml configuration files (depends on Jackson Adapter)
    * Allows nesting of files using a marker e.g `include: sub_file.yaml` would replace this line with content
      of `sub_file.yaml`
    * Reading as a system property if not found as environment variable or else the default value if it was supplied:
        * `path: ${JAVA_HOME}`
        * `myVar: ${SOME_VAR:-defaultValue}`

## TODOs

* [x] Event Sourcing
    * [x] State decider, evolver, Stream pipeline
    * [x] Sagas decider, Stream pipeline
    * [x] id safety with typed classed (StateId, CommandId, EventId)
    * [x] Events reduction
        * If using reduction the event reducer should map all states to creation event
        * Init states can't have creation events.
    * [x] Stream sharding to be used later for scaling
        * [x] Tests
        * Due to sharding (reading from **multiple** src event streams) the whole cluster should be down first before
          executing sharding, so that oldStreams are not receiving new events, while being ingested, they should be in read
          only state
* [x] Streaming
    * [x] Stream api for usage in event sourcing
    * [ ] Stream api for messaging patterns
* [ ] Increase test coverage to >85%

## Release notes

* Reactive is using `minor` (as in semantic versioning) as a major until first version is released

## Usage

All modules depend on Core. There are currently no other inter-dependencies between them.

**Disclaimer:**
> `reactive` is on edge, it's a work in progress and a pragmatic learning effort, so feel free to create issues or PRs.

```xml

<properties>
    <reactive.version>...</reactive.version>
</properties>

<dependencies>
<dependency>
    <groupId>io.memoria.reactive</groupId>
    <artifactId>core</artifactId>
    <version>${reactive.version}</version>
</dependency>

<dependency>
    <!-- replace module_name with your preferred module -->
    <groupId>io.memoria.reactive</groupId>
    <artifactId>module_name</artifactId>
    <version>${reactive.version}</version>
</dependency>
</dependencies>
```

## Contribution

You can just do pull requests, and I will check them asap.

## Related Articles

* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)
