package com.sappyoak.konscriptor.compiler

import com.sappyoak.konscriptor.logging.LogLevel
import com.sappyoak.konscriptor.compiler.script.ScriptInitializationType

/** Definition of a Script **/
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Script(
    val version: String = "",
    val logLevel: LogLevel = LogLevel.Info,
    val initialize: ScriptInitializationType = ScriptInitializationType.Eager,
    val priority: Int = 0,
    val excludedDefaultImports: Array<String> = []
)


/** Used to import and depend on other scripts **/
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Imports(vararg val script: String)

/** Used to Depend on a specific Plugin **/
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PluginDependencies(vararg val plugin: String)

/** Used to silo off a sectioned block of a script that is independent of the rest of the
 *  script
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@RequiresOptIn(message = "This API is experimental and might not be working properly and is bound to change in the future. Use at your own risk")
@MustBeDocumented
annotation class Module(val name: String)