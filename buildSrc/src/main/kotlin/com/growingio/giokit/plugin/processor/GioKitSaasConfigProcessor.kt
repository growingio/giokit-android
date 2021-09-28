package com.growingio.giokit.plugin.processor

import com.android.SdkConstants
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.SourceKind
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.mergedManifests
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.kotlinx.get
import com.didiglobal.booster.kotlinx.search
import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.artifacts
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.utils.DependLib
import com.growingio.giokit.plugin.utils.GioConfigUtils
import com.growingio.giokit.plugin.utils.GioConfigUtils.gioSaasSdks
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
class GioKitSaasConfigProcessor(val project: Project) : VariantProcessor {

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
                || dependLib.variant.contains(":vds-observable-autoburry")
                || dependLib.variant.contains(":vds-observable-burry")
            ) {
                gioSaasSdks.add(dependLib)
                "add gio dependLib:${dependLib}".println()
            }
        }

        if (variant is ApplicationVariant) {
            GioConfigUtils.saasDomain = variant.applicationId
            "App PackageName is====>${GioConfigUtils.saasDomain}".println()

            // 获取 AndroidManifest 下配置的 url scheme 如"growing.d80871b41ef40518"
            // 执行clean之后，无法再在 build 文件中找到 build/intermediates/merged_manifest/debug/AndroidManifest.xml，故无法找到 urlscheme
            // 第二次build时将会找到。。
            variant.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
                //用来标记未解析到 url scheme的情况，需要传一个非空的值给 giokit
                GioConfigUtils.saasXmlScheme = "empty"
                val parser = SAXParserFactory.newInstance().newSAXParser()
                val handler = GioKitXmlHandler {
                    GioConfigUtils.saasXmlScheme = it
                }
                parser.parse(manifest, handler)
                "App Application path====>${handler.applications}".println()
                "App Activity path====>${handler.activities}".println()
            }

            //获取插件配置
            variant.project.getAndroid<AppExtension>().let {
                val gioKit = variant.project.extensions.getByType(GioKitExtension::class.java)
                GioConfigUtils.setPluginExtension(gioKit)
            }
        } else {
            "variant isn't ApplicationVariant".println()
        }


        "processor->saasXmlScheme:${GioConfigUtils.saasXmlScheme}".println()
        "processor->saasDomain:${GioConfigUtils.saasDomain}".println()
        "processor->hasGioPluginSaas:${GioConfigUtils.hasGioPluginSaas}".println()
        "processor->gioDepend:${GioConfigUtils.gioSaasSdks}".println()
    }
}