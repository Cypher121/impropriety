[![Maven Central badge](https://img.shields.io/maven-central/v/coffee.cypher.impropriety/impropriety?style=flat-square)](https://search.maven.org/artifact/coffee.cypher.impropriety/impropriety)

# Impropriety
A JVM parser for [Improperties](https://github.com/FoundationGames/Improperties-Specification).

## Getting started

### Gradle

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("coffee.cypher.impropriety:impropriety:1.+")
}
```

### Maven
```xml
<dependency>
    <groupId>coffee.cypher.impropriety</groupId>
    <artifactId>impropriety</artifactId>
    <version>1.0.0</version>
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

