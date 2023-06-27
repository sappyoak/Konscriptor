package com.sappyoak.konscriptor.compiler.dependencies

import java.io.File

data class ExternalDependencies(
    val compiled: Set<File>,
    val sources: Set<File>
)