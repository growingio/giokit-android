package com.growingio.giokit.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.processor.GioKitConfigProcessor
import com.growingio.giokit.plugin.transform.v3.GioKitV3Transform
import com.growingio.giokit.plugin.utils.GioConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/8/17
 */
class GioKitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("giokitExt", GioKitExtension::class.java)

        val v3Config = GioConfig("v3")
        v3Config.hasGioPlugin = project.plugins.hasPlugin("com.growingio.android.autotracker")

        //主工程项目
        if (project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature")) {
            val appExtension = project.extensions.getByName("android") as AppExtension

            appExtension.registerTransform(GioKitV3Transform(project, v3Config))

            project.afterEvaluate {
                appExtension.applicationVariants.forEach { variant ->
                    //兼容单项目多 application 情况
                    if (it.plugins.hasPlugin("com.growingio.giokit")) {
                        GioKitConfigProcessor(it, v3Config).process(variant)
                    }
                }
            }
        }

        if (project.plugins.hasPlugin("com.android.library")) {
            val libraryExtension = project.extensions.getByName("android") as LibraryExtension
            libraryExtension.registerTransform(GioKitV3Transform(project, v3Config))
            project.afterEvaluate {
                libraryExtension.libraryVariants.forEach { variant ->
                    if (it.plugins.hasPlugin("com.growingio.giokit")) {
                        GioKitConfigProcessor(it, v3Config).process(variant)
                    }
                }
            }
        }
    }
}