package com.sappyoak.konscriptor.compiler.config

import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jdkHome
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.util.PropertiesCollection

import java.io.File

import com.sappyoak.konscriptor.compiler.script.KonscriptorScriptInterface
import com.sappyoak.konscriptor.compiler.script.ImplicitScriptImports


class CompilationConfiguration : ScriptCompilationConfiguration()

operator fun ScriptCompilationConfiguration.invoke(): ScriptCompilationConfiguration = createCompilationConfig()

fun createCompilationConfig(): ScriptCompilationConfiguration = ScriptCompilationConfiguration {
    baseClass(KonscriptorScriptInterface::class)
    defaultImports(ImplicitScriptImports.defaults)
    jvm {
        dependenciesFromClassContext(CompilationConfiguration::class, wholeClasspath = true)
        compilerOptions()
    }
}