package com.growingio.giokit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.kotlinx.get
import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.artifacts
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.utils.DependLib
import com.growingio.giokit.plugin.utils.GioConfigUtils
import com.growingio.giokit.plugin.utils.GioConfigUtils.dependLibs
import com.growingio.giokit.plugin.utils.GioConfigUtils.gioSdks
import com.growingio.giokit.plugin.utils.isRelease
import com.growingio.giokit.plugin.utils.println
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import javax.xml.parsers.SAXParserFactory

/**
 * <p>
 *     通过插件获取项目的配置
 * @author cpacm 2021/8/17
 */
class GioKitPluginConfigProcessor(val project: Project) : VariantProcessor {
    override fun process(variant: BaseVariant) {
        if (variant.isRelease()) return

        //获取项目是否接入GrowingIO SDK
        dependLibs.clear()
        val dependencies = variant.dependencies
        for (artifactResult: ResolvedArtifactResult in dependencies) {
            val variants = artifactResult.variant.displayName.split(" ")
            var dependLib: DependLib
            if (variants.size == 3) {
                dependLib = DependLib(
                    variants[0],
                    artifactResult.file.length()
                )
                dependLibs.add(dependLib)
            } else if (variants.size == 4) {
                dependLib = DependLib(
                    "porject ${variants[1]}",
                    artifactResult.file.length()
                )
                dependLibs.add(dependLib)
            }
        }
        gioSdks.clear()
        for (dependLib in dependLibs) {
            if (dependLib.variant.contains(":growingio")|| dependLib.variant.contains(":gio-sdk")) {
                gioSdks.add(dependLib)
                "add gio dependLib:${dependLib}".println()
            }
        }

        if (variant is ApplicationVariant) {
            // 获取 AndroidManifest 下配置的 url scheme 如"growing.d80871b41ef40518"
            variant.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
                val parser = SAXParserFactory.newInstance().newSAXParser()
                val handler = GioKitXmlHandler()
                parser.parse(manifest, handler)
                GioConfigUtils.defaultDomain = handler.appPackageName
                "App PackageName is====>${handler.appPackageName}".println()
                "App Application path====>${handler.applications}".println()
                "App Activity path====>${handler.activities}".println()
            }

            //获取插件配置
            variant.project.getAndroid<AppExtension>().let {
                val gioKit = variant.project.extensions.getByType(GioKitExtension::class.java)
                GioConfigUtils.setPluginExtension(gioKit)
            }
        }

        GioConfigUtils.hasGioPlugin = project.plugins.hasPlugin("com.growingio.android.autotracker")

    }
}