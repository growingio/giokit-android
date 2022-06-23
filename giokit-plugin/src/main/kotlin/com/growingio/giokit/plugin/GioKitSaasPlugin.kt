package com.growingio.giokit.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.growingio.android.plugin.util.ComponentCompat
import com.growingio.android.plugin.util.SimpleAGPVersion
import com.growingio.android.plugin.util.getAndroidComponentsExtension
import com.growingio.android.plugin.utils.info
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.processor.GioKitConfigProcessor
import com.growingio.giokit.plugin.transform.saas.GiokitSaasFactory
import com.growingio.giokit.plugin.transform.saas.GiokitSaasTransform
import com.growingio.giokit.plugin.utils.GioConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2021/8/17
 */
class GioKitSaasPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val giokitExtension = project.extensions.create("giokitExt", GioKitExtension::class.java)

        project.plugins.withType(AndroidBasePlugin::class.java) {
            if (!giokitExtension.enableRelease && isReleaseTask(project)) {
                return@withType
            }

            val sassConfig = GioConfig("saas")
            sassConfig.hasGioPlugin = project.plugins.hasPlugin("com.growingio.android")

            val androidExtension = project.extensions.findByType(BaseExtension::class.java)
                ?: error("Android BaseExtension not found.")
            androidExtension.forEachRootVariant { variant ->
                GioKitConfigProcessor(project, sassConfig).process(variant)
            }

            //configureBytecodeTransform(project, sassConfig)

            if (SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION < SimpleAGPVersion(4, 2)) {
                // Configures bytecode transform using older APIs pre AGP 4.2
                configureBytecodeTransform(project, sassConfig)
            } else if (SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION >= SimpleAGPVersion(7, 1)
                && SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION < SimpleAGPVersion(7, 3)
            ) {
                // AndroidGradlePlugin version 7.2 breaks transform API when used along with ASM API
                // look at https://issuetracker.google.com/issues/232438924
                configureBytecodeTransform(project, sassConfig)
            } else {
                configureBytecodeTransformASM(project, sassConfig)
            }
        }
    }

    private fun configureBytecodeTransform(project: Project, v3Config: GioConfig) {
        val androidExtension =
            project.extensions.findByType(BaseExtension::class.java) ?: error("Android BaseExtension not found.")
        androidExtension::class.java.getMethod(
            "registerTransform",
            Class.forName("com.android.build.api.transform.Transform"),
            Array<Any>::class.java
        ).invoke(androidExtension, GiokitSaasTransform(project, androidExtension, v3Config), emptyArray<Any>())
    }

    private fun BaseExtension.forEachRootVariant(@Suppress("DEPRECATION") block: (variant: com.android.build.gradle.api.BaseVariant) -> Unit) {
        when (this) {
            is AppExtension -> {
                applicationVariants.all {
                    block(it)
                }
            }
            is LibraryExtension -> {
                libraryVariants.all {
                    block(it)
                }
            }
            else -> info("Ignore other variant.")
        }
    }

    private fun configureBytecodeTransformASM(project: Project, saasConfig: GioConfig) {
        fun registerTransform(androidComponent: ComponentCompat) {
            androidComponent.transformClassesWith(
                classVisitorFactoryImplClass = GiokitSaasFactory::class.java,
                scope = InstrumentationScope.ALL
            ) { params ->
                //val classesDir = File(project.buildDir, "intermediates/javac/${androidComponent.name}/classes")
                val classesDir = File(project.buildDir, "generated/source/buildConfig/${androidComponent.name}/")
                params.additionalClassesDir.set(classesDir)
                params.gioConfig.set(saasConfig)
            }
            androidComponent.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
        getAndroidComponentsExtension(project).onAllVariants {
            registerTransform(it)
        }
    }

    private fun isReleaseTask(project: Project): Boolean {
        return project.gradle.startParameter.taskNames.any {
            it.contains("release") || it.contains("Release")
        }
    }
}