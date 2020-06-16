
[![Build](https://github.com/memoria-io/jutils/workflows/Build/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3Abuild)
[![Publish](https://github.com/memoria-io/jutils/workflows/Publish/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3APublish)
[![Codecove](https://codecov.io/github/memoria-io/jutils/coverage.svg?precision=2)](https://codecov.io/gh/memoria-io/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/)

# jutils
`jutils` is a functional reactive utility library, an opinionated and pragmatic learning effort.
It was created because I found much reused code after combining multiple libraries.

One example is the combination of [Project Reactor](https://projectreactor.io/) along with [VAVR](https://vavr.io), it
became clear that there was a need for functional utility methods like the ones in
[functional](core/src/main/java/io/memoria/jutils/core/utils/functional) package.


## Features
* Vavr + Reactor Functional utilities
* CQRS and Eventsourcing utilities.
* Simple Reactive Message bus implementation for:
  * Apache Kafka
  * Nats
  * Apache Pulsar
* Reactor Netty adapter with error handling escalation
* Yaml adapter with features e.g nesting, fluent api, resource and file loading
* Vavr GSON Json implementation
* Argon2 Hashing adapter
* In memory generic crud

## Related Articles
* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)

## Usage
All modules depend on core. There are currently no other inter-dependencies between them.

```xml
<properties>
    <jutils.version>0.7.8</jutils.version>
</properties>
<dependencies>
    <dependency>
        <groupId>io.memoria.jutils</groupId>
        <artifactId>core</artifactId>
        <version>${jutils.version}</version>
    </dependency>

    <dependency>
        <!-- replace module_name with your preferred module -->
        <groupId>io.memoria.jutils</groupId>
        <artifactId> module_name </artifactId>
        <version>${jutils.version}</version>
    </dependency>
</dependencies>
```


## Contribution
You can just do pull requests and I will check them asap.

## Todos


## Release notes
* v0.7.0-8
    * Using records
    * Adding new methods to NettyHttpUtils
    * Improving and decoupling classes in Kafka, Pulsar, and Nats
    * Decoupling Argon verifier from Argon Hasher
* v0.6.+
    * Using Openjdk14
    * Moving to Github actions
    * Fixing and clean up
* v0.5.5
    * ETCD client wrapper
* v0.5.0
    * Back to using EventHandler and CommandHandlers 
        * Using thin DTOs for lower coupling between Event/Command DTOs and Entity
        * Also there would be more reusability of DTOs by creating more Event and command handlers
* v0.4.9
    * Marking eventsourcing interfaces as DTOs
* v0.4.7
    * Fixing hashcode and equals bugs
    * Consolidating the use of handlers into apply method by making events/commands implement vavr's Function1.
* v0.4.2
    * Argon hashing with Mono
* v0.4.1
  * Split to modules for better dependency handling
* v0.3.7
  * Fix missing jacoco.exe file
  * Add coverage for uncovered utilities
* v0.3.5
  * Initial EventSourcing implementation
  * Fix infinite wait in kafka producer
* v0.3.4
  * Initial short implementation of Kafka, Nats and Pulsar APIs with Reactor and Vavr
