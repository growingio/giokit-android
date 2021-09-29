package com.growingio.giokit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.artifacts
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.utils.DependLib
import com.growingio.giokit.plugin.utils.GioConfig
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
class GioKitConfigProcessor(val project: Project, private val gioConfig: GioConfig) : VariantProcessor {

    private val dependLibs = mutableSetOf<DependLib>()

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
        for (dependLib in dependLibs) {
            dependLib.variant.println()
            //兼容2.0
            if (dependLib.variant.contains("com.growingio.android")
                || dependLib.variant.contains(":growingio")
                || dependLib.variant.contains(":gio-sdk")
                || dependLib.variant.contains(":vds-observable-autoburry")
                || dependLib.variant.contains(":vds-observable-burry")
            ) {
                gioConfig.gioSdks.add(dependLib)
                "add gio dependLib:${dependLib}".println()
            }
        }

        if (variant is ApplicationVariant) {
            gioConfig.domain = variant.applicationId
            "App PackageName is====>${gioConfig.domain}".println()

            // 获取 AndroidManifest 下配置的 url scheme 如"growing.d80871b41ef40518"
            // 执行clean之后，无法再在 build 文件中找到 build/intermediates/merged_manifest/debug/AndroidManifest.xml，故无法找到 urlscheme
            // 第二次build时将会找到。。
            variant.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
                //用来标记未解析到 url scheme的情况，需要传一个非空的值给 giokit
                gioConfig.xmlScheme = "empty"
                val parser = SAXParserFactory.newInstance().newSAXParser()
                val handler = GioKitXmlHandler {
                    gioConfig.xmlScheme = it
                }
                parser.parse(manifest, handler)
                "App Application path====>${handler.applications}".println()
                "App Activity path====>${handler.activities}".println()
            }

            //获取插件配置
            variant.project.getAndroid<AppExtension>().let {
                val gioKit = variant.project.extensions.getByType(GioKitExtension::class.java)
                gioConfig.setPluginExtension(gioKit)
            }
        } else {
            "variant isn't ApplicationVariant".println()
        }

        "processor->saasXmlScheme:${gioConfig.xmlScheme}".println()
        "processor->saasDomain:${gioConfig.domain}".println()
        "processor->hasGioPluginSaas:${gioConfig.hasGioPlugin}".println()
        "processor->gioDepend:${gioConfig.gioSdks}".println()
    }
}