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


rootProject.name = "konscriptor-root"

include("common")
include("paper-kotlin-dsl")
include("script-compiler")
include("script-host")
include ("konscriptor")
include("konscriptor-paper-plugin")