package com.sappyoak.konscriptor.compiler.dependencies


import kotlin.script.experimental.api.*
import kotlin.script.experimental.api.flatMapSuccess
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.impl.resolve
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.util.filterByAnnotationType
import java.io.File

class ScriptDependencyResolver() {
    private val mavenResolver: MavenDependenciesResolver by lazy { MavenDependenciesResolver() }
    private val fileResolver: FileSystemDependenciesResolver by lazy { FileSystemDependenciesResolver() }

    // Need to do some caching here
    fun resolveExternalDependencies(source: SourceCode, annotations: List<Annotation>): ExternalDependencies {
        TODO()
    }

    private suspend fun ExternalDependenciesResolver.resolveSourceFromScriptAnnotations(annotations: Iterable<ScriptSourceAnnotation<*>>): ResultWithDiagnostics<List<File>> {
        val reports = mutableListOf<ScriptDiagnostic>()
        for ((annotation, locationWithId) in annotations) {
            when (annotation) {
                // Check about caching
                is Repository -> {
                    for (coordinates in annotation.repositoriesCoordinates) {
                        val added = addRepository(coordinates, ExternalDependenciesResolver.Options.Empty, locationWithId)
                            .also { reports.addAll(it.reports) }
                            .valueOr { return it }
                        if (!added) {
                            return reports + makeFailureResult("Unrecognized repository coordinates: $coordinates", locationWithId = locationWithId)
                        }
                    }

                }
                is DependsOn -> {}
                else -> return reports + makeFailureResult("Unknown annotation ${annotation.javaClass}", locationWithId = locationWithId)
            }
        }

        return reports + annotations.filterByAnnotationType<DependsOn>()
            .flatMapSuccess { (annotation, locationWithId) ->
                annotation.artifactsCoordinates.asIterable().flatMapSuccess { artifactCoorindates ->
                    resolve(artifactAsSource(artifactCoorindates), ExternalDependenciesResolver.Options.Empty, locationWithId)
                }
            }
    }

    private suspend fun ExternalDependenciesResolver.resolveSourceFromAnnotations(annotations: Iterable<Annotation>): ResultWithDiagnostics<List<File>> {
        val scriptSourceAnnotations = annotations.map { ScriptSourceAnnotation(it, null) }
        return resolveSourceFromScriptAnnotations(scriptSourceAnnotations)
    }

    private fun artifactAsSource(str: String): String {
        return if (str.count { it == ':' } == 2) {
            val lastColon = str.lastIndexOf(':')
            str.toMutableList().apply { addAll(lastColon, "jar:sources".toList()) }.joinToString("")
        } else str
    }

}