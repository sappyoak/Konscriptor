dependencies {
    compileOnly(libs.paper.api)

    api(libs.kotlinx.coroutines)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")

    testImplementation(kotlin("test"))
}