dependencies {
    compileOnly(libs.paper.api)
    api(libs.kotlinx.coroutines)

    implementation(libs.kotlinx.uuid)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
    implementation(project(":logging"))

    testImplementation(kotlin("test"))
}