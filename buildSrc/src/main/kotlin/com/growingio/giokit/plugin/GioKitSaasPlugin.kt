package com.growingio.giokit.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.processor.GioKitSaasConfigProcessor
import com.growingio.giokit.plugin.transform.saas.GioKitSaasTransform
import com.growingio.giokit.plugin.utils.GioConfigUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/8/17
 */
class GioKitSaasPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("giokitExt", GioKitExtension::class.java)
        //对 release task 不作处理
        if (isReleaseTask(project)) return

        GioConfigUtils.hasGioPluginSaas = project.plugins.hasPlugin("com.growingio.android")
        //主工程项目
        if (project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature")) {
            val appExtension = project.extensions.getByName("android") as AppExtension

            appExtension.registerTransform(GioKitSaasTransform(project))

            project.afterEvaluate {
                appExtension.applicationVariants.forEach { variant ->
                    if (it.plugins.hasPlugin("com.growingio.giokit.saas")) {
                        GioKitSaasConfigProcessor(it).process(variant)
                    }
                }
            }
        }

        if (project.plugins.hasPlugin("com.android.library")) {
            val libraryExtension = project.extensions.getByName("android") as LibraryExtension
            libraryExtension.registerTransform(GioKitSaasTransform(project))
            project.afterEvaluate {
                libraryExtension.libraryVariants.forEach { variant ->
                    if (it.plugins.hasPlugin("com.growingio.giokit.saas")) {
                        GioKitSaasConfigProcessor(it).process(variant)
                    }
                }
            }
        }
    }

    private fun isReleaseTask(project: Project): Boolean {
        return project.gradle.startParameter.taskNames.any {
            it.contains("release") || it.contains("Release")
        }
    }
}