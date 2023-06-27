dependencies {
    compileOnly(libs.paper.api)

    implementation(project(":logging"))
    implementation(libs.kotlinx.coroutines)

    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation(kotlin("scripting-compiler-impl-embeddable"))
    implementation(kotlin("scripting-dependencies"))
    implementation(kotlin("scripting-dependencies-maven"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))
}