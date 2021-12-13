package com.growingio.giokit.plugin.base

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import java.io.File

val SCOPE_PROJECT: MutableSet<in QualifiedContent.Scope> = TransformManager.PROJECT_ONLY

val SCOPE_FULL_PROJECT: MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

val SCOPE_FULL_WITH_FEATURES: MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_WITH_FEATURES

val SCOPE_FULL_LIBRARY_WITH_FEATURES: MutableSet<in QualifiedContent.Scope> =
    (TransformManager.SCOPE_FEATURES + QualifiedContent.Scope.PROJECT).toMutableSet()

inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T

@Suppress("UNCHECKED_CAST")
fun <T> Project.getProperty(name: String, defaultValue: T): T {
    val value = findProperty(name) ?: return defaultValue
    return when (defaultValue) {
        is Boolean -> if (value is Boolean) value as T else value.toString().toBoolean() as T
        is Byte -> if (value is Byte) value as T else value.toString().toByte() as T
        is Short -> if (value is Short) value as T else value.toString().toShort() as T
        is Int -> if (value is Int) value as T else value.toString().toInt() as T
        is Float -> if (value is Float) value as T else value.toString().toFloat() as T
        is Long -> if (value is Long) value as T else value.toString().toLong() as T
        is Double -> if (value is Double) value as T else value.toString().toDouble() as T
        is String -> if (value is String) value as T else value.toString() as T
        else -> value as T
    }
}

/**
 * Returns the corresponding variant of this transform invocation
 *
 * @author johnsonlee
 */
fun TransformInvocation.getVariant(project: Project): BaseVariant {
    return project.getAndroid<BaseExtension>().let { android ->
        this.context.variantName.let { variant ->
            when (android) {
                is AppExtension -> when {
                    variant.endsWith("AndroidTest") -> android.testVariants.single { it.name == variant }
                    variant.endsWith("UnitTest") -> android.unitTestVariants.single { it.name == variant }
                    else -> android.applicationVariants.single { it.name == variant }
                }
                is LibraryExtension -> android.libraryVariants.single { it.name == variant }
                else -> TODO("variant not found")
            }
        }
    }
}


fun TransformInvocation.getBootClasspath(project: Project): Collection<File> {
    return project.getAndroid<BaseExtension>().bootClasspath
}

/**
 * Returns the compile classpath of this transform invocation
 *
 * @author johnsonlee
 */
val TransformInvocation.compileClasspath: Collection<File>
    get() = listOf(inputs, referencedInputs).flatten().map {
        it.jarInputs + it.directoryInputs
    }.flatten().map {
        it.file
    }

/**
 * Returns the runtime classpath of this transform invocation
 *
 * @author johnsonlee
 */
fun TransformInvocation.getRuntimeClasspath(project: Project): Collection<File> {
    return compileClasspath + getBootClasspath(project)
}

fun TransformInvocation.isDataBindingEnabled(project: Project): Boolean {
    return project.getAndroid<BaseExtension>().dataBinding.isEnabled
}
