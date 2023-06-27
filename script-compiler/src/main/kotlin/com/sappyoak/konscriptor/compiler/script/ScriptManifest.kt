package com.sappyoak.konscriptor.compiler.script

import java.io.Serializable
import com.sappyoak.konscriptor.logging.LogLevel

/**
 * A Class that provides all the relevant metadata about a Script after it has been processed in pre-compilation
 * and had all of its annotations analyzed
 */
data class ScriptManifest(
    val version: String,
    val pluginDependencies: Set<String>,
    val dependencyFiles: Set<String>,
    val excludedDefaultImports: Set<String>,
    val initializationType: ScriptInitializationType = ScriptInitializationType.Eager,
    val priority: Int = 0,
    var logLevel: LogLevel,
) : Serializable