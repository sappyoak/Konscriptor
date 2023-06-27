package com.sappyoak.konscriptor.compiler.script

import java.io.File

import com.sappyoak.konscriptor.logging.Logger

/**
 * This is the base interface for all KonscriptorScript's. As of now there is only the base
 * [KonscriptorScript], but this allows for easy extensibility in the future.
 *
 * By provided the generic type to the interface, we open up the doors for smaller, "scoped" functions that
 * can be executed in a much more condensed context for specific functionality and incremental compilation.
 */

interface KonscriptorScriptInterface<T> {
    val logger: Logger
    val manifest: ScriptManifest
    val name: String
    val context: T
    val dataFolder: File
    val scriptFolder: File
}