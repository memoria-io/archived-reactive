
[![Build](https://github.com/memoria-io/jutils/workflows/Build/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3Abuild)
[![Publish](https://github.com/memoria-io/jutils/workflows/Publish/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3APublish)
[![Codecove](https://codecov.io/github/memoria-io/jutils/coverage.svg?precision=2)](https://codecov.io/gh/memoria-io/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/)

# JUtils
`jutils` is a tool-box that makes it easier and faster to build reactive applications.
It relies heavily on [Reactive Streams](https://www.reactive-streams.org/) and uses e.g [Project-Reactor](https://projectreactor.io/),
[Reactor Netty](https://github.com/reactor/reactor-netty) and functional paradigms and collections from [Vavr](https://www.vavr.io/), 

## Features
* Vavr + Reactor Functional utilities
* Reactor Netty utility functions
* Yaml configuration loader utility
    * Nesting capability using `include: foo/bar.yaml`
    * YamlConfigMap parsing utility
* Reactive wrapper for messaging:
    * Apache Kafka
    * Nats
    * Apache Pulsar
* Reactive wrapper for a Keyvalue store 
    * Etcd adapter  
* Json 
    * Google guice adapter
* Reactive CQRS and Eventsourcing utilities (alpha stage)
* Reactive functional in-memory generic cruds (not for production)
* `jutils` is up to date with the latest versions of JDK.


## Usage
All modules depend on Core. There are currently no other inter-dependencies between them.

```xml
<properties>
    <jutils.version>0.9.82</jutils.version>
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

## Notes

**Why I moved jutils from my own github account to become under Memoria ?**
> I believe in the power of community and ownership, so this step was taken encourage people to contribute and be part of something 
> they feel they own, and that their contributions are not wasted to an individual but to a team they're part of.

**Using latest JDKs**

`jutils` is using latest JDK with preview features, currently `14`, the reason for this is that Java is now improving on a much faster rate
than it used to, new features like `records` are saving a lot of code space.  

**About jutils CI/CD:**

`jutils` refactoring and updates is very fast, thanks to Github actions and before that was Travis, 
I worked hard to maintain a 5~minute full pipeline cycle - from code to Maven release to the public.
A bar of >90% test coverage would hopefully become stable soon, to allow much safer refactoring efforts.

**Disclaimer:**
> `jutils` is on edge, it's a work in progress and a pragmatic learning effort, please be aware there might be some opinionated or even wrong decisions
> so feel free to create issues or PRs. There will be much better care for breaking changes, soon as the library gets more users and traffic.


## Related Articles
* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)

## Contribution
You can just do pull requests, and I will check them asap.
