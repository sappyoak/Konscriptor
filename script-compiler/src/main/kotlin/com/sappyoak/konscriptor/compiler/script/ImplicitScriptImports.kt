package com.sappyoak.konscriptor.compiler.script

import com.sappyoak.konscriptor.compiler.unsafeLazy

/**
 * @TODO be able to widen and condense this dependency scope depending on the version of current plugins/server-software
 * installed on host machine. Reducing scope will be really annoying with the wildcard imports though.
 * It might be better to autogenerate the imports?
 */

private fun getPaperImports() = listOf(
    "org.bukkit.*",
    "org.bukkit.block.*",
    "org.bukkit.command.*",
    "org.bukkit.configuration.*",
    "org.bukkit.entity.*",
    "org.bukkit.event.*"
)

private fun konscriptorScriptImports() = listOf(
    "com.sappyoak.konscriptor.compiler.Script",
    "com.sappyoak.konscriptor.compiler.Imports",
    "com.sappyoak.konscriptor.compiler.PluginDependencies",
    "com.sappyoak.konscriptor.compiler.Module"
)


private fun jvmLibImports() = listOf(
    "kotlin.time.*",
    "kotlin.math.*",
    "java.io.*",
    "java.util.*",
    "java.util.concurrent.*",
    "java.util.concurrent.atomic.*",
    "kotlinx.coroutines.*",
    "kotlinx.coroutines.flow.*",
    "kotlinx.coroutines.channels.*",
    "kotlinx.coroutines.selects.*",
    "kotlin.script.experimental.dependencies.DependsOn",
    "kotlin.script.experimental.dependencies.Repository"
)

object ImplicitScriptImports {
    val defaults by unsafeLazy {
        getPaperImports() + konscriptorScriptImports() + jvmLibImports()
    }

    fun applySelectiveScope(filter: Regex) = defaults.filterNot { filter.matches(it) }
}