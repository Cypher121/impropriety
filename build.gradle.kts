import java.net.URL

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    antlr
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
    id("org.jetbrains.dokka")
    `java-test-fixtures`
}

group = "coffee.cypher.impropriety"
version = "1.0.1"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    modules {
        module("spek:spek-runtime") {
            replacedBy(libs.spek.runtime.get().module, "Spek has new central coordinates")
        }

        module("spek:spek-dsl") {
            replacedBy(libs.spek.dsl.get().module, "Spek has new central coordinates")
        }
    }

    antlr(libs.antlr.full)
    implementation(libs.antlr.runtime)
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.2")

    testImplementation(libs.bundles.testing.core)
    testRuntimeOnly(libs.bundles.testing.runtime)

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

}

//fix antlr plugin's mess
configurations.api {
    setExtendsFrom(extendsFrom.filterNot { it.name == "antlr" })
}

sourceSets.all {
    java {
        setSrcDirs(srcDirs.filterNot { "generated-src" in it.path })
        srcDir(layout.buildDirectory.dir("generated/sources/antlr/${this@all.name}"))
    }
}

tasks {
    //configure antlr
    val relocateSources = register<Copy>("relocateSources") {
        dependsOn(generateGrammarSource)

        from(layout.buildDirectory.dir("generated-src/antlr"))
        into(layout.buildDirectory.dir("generated/sources/antlr"))

        filter {
            it.replace("public class", "class")
                .replace("public interface", "interface")
        }
    }

    generateGrammarSource {
        arguments = listOf("-visitor")
    }

    //use dokka for javadoc
    withType<Jar> {
        if ("javadoc" in archiveClassifier.get()) {
            dependsOn(dokkaJavadoc)
        }
    }

    withType<Javadoc> {
        enabled = false
    }

    //depend on processed antlr sources
    named("sourcesJar") {
        dependsOn(relocateSources)
    }

    named("compileKotlin") {
        dependsOn(relocateSources)
    }

    dokkaJavadoc {
        dependsOn(relocateSources)

        dokkaSourceSets.all {
            reportUndocumented.set(true)

            sourceLink {
                localDirectory.set(file("src/$name/kotlin"))
                remoteLineSuffix.set("#L")
                remoteUrl.set(URL("https://github.com/Cypher121/impropriety/blob/master/src/$name/kotlin"))
            }
        }
    }

    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.2-rc-3"
    }
}

dependencyLocking.lockAllConfigurations()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            suppressAllPomMetadataWarnings()

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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
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
