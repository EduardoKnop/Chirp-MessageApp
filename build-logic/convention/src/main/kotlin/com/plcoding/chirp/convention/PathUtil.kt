package com.plcoding.chirp.convention

import org.gradle.api.Project
import java.util.Locale

internal fun Project.pathToPackageName() : String {
    val relativePackageName = path
        .replace(':', '.')
        .lowercase()
    
    return "com.plcoding$relativePackageName"
}

internal fun Project.pathToResourcePrefix() : String {
    return path
        .replace(':', '_')
        .lowercase()
        .drop(1) + "_"
}

internal fun Project.pathToFrameworkName(): String {
    val parts = this.path.split(":", "-", "_", " ")
    return parts.joinToString("") { part ->
        part.replaceFirstChar { it.titlecase(Locale.ROOT) }
    }
}