[![Maven Central badge](https://img.shields.io/maven-central/v/coffee.cypher.impropriety/impropriety?style=flat-square)](https://search.maven.org/artifact/coffee.cypher.impropriety/impropriety)
[![Build and check](https://github.com/Cypher121/impropriety/actions/workflows/build-and-check.yml/badge.svg)](https://github.com/Cypher121/impropriety/actions/workflows/build-and-check.yml)

# Impropriety
A JVM parser for [Improperties](https://github.com/FoundationGames/Improperties-Specification).

## Getting started

### Gradle

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("coffee.cypher.impropriety:impropriety:latest.release")
}
```

### Maven
```xml
<dependency>
    <groupId>coffee.cypher.impropriety</groupId>
    <artifactId>impropriety</artifactId>
    <version>RELEASE</version>
</dependency>
```

## Usage examples

### Java

```java
//reading from a file
ImpropertiesReader reader = ImpropertiesReader.fromFile("some/file.improperties");

ImpObject tree = reader.toObject();

String value = tree.get("list").get(0).get("value").asValue().get();

//writing to a file
ImpropertiesWriter writer = new ImpropertiesWriter.Builder()
        .indent(2)
        .topLevelSpacing(1)
        .innerSpacing(1)
        .keyValueJoiner(": ")
        .build();

Map<String, Object> data = someDataSource();

writer.writeToFile(data, "some_other/file.improperties")
```

### Kotlin

```kotlin
//reading from a file
val reader = ImpropertiesReader.fromFile("some/file.improperties")
val tree = reader.toObject()
val value = tree["list"][0]["value"].asValue().get()

//writing to a file
val writer = ImpropertiesWriter(
    indent = 2,
    topLevelSpacing = 1,
    innerSpacing = 1,
    keyValueJoiner = ": "
)

val data: Map<String, Any> = someDataSource()
writer.writeToFile(data, "some_other/file.improperties")
```

## Limitations

* List markers must be followed by whitespace (`- element`, not `-element`)

## Using test fixtures in other projects

If you wish to use Impropriety's test fixtures in another project using Gradle,
you can add them as a test dependency as follows.

```kotlin
dependencies {
    testImplementation(testFixtures("coffee.cypher.impropriety:impropriety:latest.release"))
}
```
