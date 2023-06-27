pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}


rootProject.name = "konscriptor"

include("paper-kotlin-dsl")
include("script-compiler")
include("logging")
