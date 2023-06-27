import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
}

group = "com.sappyoak"
description = "A scripting platform for Paper using a custom DSL with kotlin-scripting"
version = "1.0-SNAPSHOT"

val mcVersion: String by properties

allprojects {
    group = "com.sappyoak"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks.withType<KotlinCompile> {
        kotlinOptions.apply {
            freeCompilerArgs = listOf("-Xcontext-receivers")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
