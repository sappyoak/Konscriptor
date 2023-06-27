package com.sappyoak.konscriptor.compiler.script

/**
 * Defines how the script and it's dependencies should be treated in the compilation and evaluation process.
 *
 * [ScriptInitializationType.Conditional]:  The script will wait until a certain condition is fulfilled before initializing itself and executing
 *                                          Annotations are still processed, and dependencies are queued for async loading rather than loading up-front
 * [ScriptInitializationType.Deferred]:     By default scripts will start loading as soon as possible of the first available tick once the core plugin
 *                                          starts. A Deferred script waits until after this first loading pass has completed before beginning
 *                                          to load and execute. This is great for scripts not in the "hot path" of execution.
 *                                          Annotations are processed but dependencies are not loaded until the phase where this script is loaded
 * [ScriptInitializationType.Eager]:        The default value. Scripts begin being compiled and loading their dependencies as soon as possible
 * [ScriptInitializationType.Ignored]:      This marks a script as DoNotProcess. It's Annotations will be parsed and meta information stored,
 *                                          but it will *never* be compiled by the compiler on its own. This is for exposed triggers
 *                                          and hooks that need to dynamically trigger script loading and execution with more fine-grained control
 *                                          than a conditional
 */
enum class ScriptInitializationType {
    Conditional,
    Deferred,
    Eager,
    Ignored;
}