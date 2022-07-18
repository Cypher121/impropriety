import java.net.URL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    antlr
    `maven-publish`
    signing
    `java-test-fixtures`

    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nexus)
}

group = "coffee.cypher.impropriety"
version = "1.0.1"

kotlin {
    explicitApi()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    antlr(libs.antlr.full)
    implementation(libs.antlr.runtime)
    implementation(libs.kotlin.stdlib)

    testImplementation(libs.bundles.testing.core)
    testRuntimeOnly(libs.bundles.testing.runtime)

    testFixturesImplementation(libs.jackson.kotlin)
}

// SEE: https://github.com/gradle/gradle/issues/820
// avoids adding a runtime dependency on full antlr build tools
configurations.api {
    setExtendsFrom(extendsFrom.filterNot { it.name == "antlr" })
}

//any source sets using antlr sources should use relocated ones instead
sourceSets.all {
    java {
        if (srcDirs.any { "generated-src" in it.path }) {
            setSrcDirs(srcDirs.filterNot { "generated-src" in it.path })
            srcDir(layout.buildDirectory.dir("generated/sources/antlr/${this@all.name}"))
        }
    }
}

tasks {
    // replaces generated antlr sources with
    // reduced visibility versions
    val relocateSources by registering(Copy::class) {
        dependsOn(generateGrammarSource)

        from(layout.buildDirectory.dir("generated-src/antlr"))
        into(layout.buildDirectory.dir("generated/sources/antlr"))

        filter {
            it.replace("public class", "class")
                .replace("public interface", "interface")
        }
    }

    //use kotlin versions instead!
    named("javadocJar") {
        dependsOn(dokkaJavadoc)
    }

    javadoc {
        enabled = false
    }

    named("sourcesJar") {
        dependsOn(relocateSources)
    }

    withType<KotlinCompile> {
        dependsOn(relocateSources)

    }

    dokkaJavadoc {
        dependsOn(relocateSources)

        dokkaSourceSets.named("main") {
            reportUndocumented.set(true)

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteLineSuffix.set("#L")
                remoteUrl.set(URL("https://github.com/Cypher121/impropriety/blob/master/src/main/kotlin"))
            }
        }
    }

    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

dependencyLocking.lockAllConfigurations()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Impropriety")
                description.set("Improperties parser for JVM")
                url.set("https://github.com/Cypher121/impropriety/")

                scm {
                    connection.set("scm:git:git://github.com/Cypher121/impropriety.git")
                    developerConnection.set("scm:git:ssh://github.com/Cypher121/impropriety.git")
                    url.set("https://github.com/Cypher121/impropriety/")
                }

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("cypher121")
                        name.set("Cypher121")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

signing {
    sign(publishing.publications["maven"])
}