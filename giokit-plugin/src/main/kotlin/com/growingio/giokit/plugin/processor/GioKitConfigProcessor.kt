package com.growingio.giokit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.growingio.giokit.plugin.base.getAndroid
import com.growingio.giokit.plugin.extension.GioKitExtension
import com.growingio.giokit.plugin.utils.*
import org.gradle.api.Project
import javax.xml.parsers.SAXParserFactory

/**
 * <p>
 *     通过插件获取项目的配置
 * @author cpacm 2021/8/17
 */
class GioKitConfigProcessor(private val project: Project, private val gioConfig: GioConfig) : VariantProcessor {

    private val dependLibs = mutableSetOf<DependLib>()

    override fun process(variant: BaseVariant) {
        if (!gioConfig.gioKitExt.enableRelease && variant.isRelease()) return

        //获取项目是否接入GrowingIO SDK
        dependLibs.clear()
        getGrowingioDepends()
        for (dependLib in dependLibs) {
            //兼容2.0
            if (dependLib.variant.contains("com.growingio.android")
                || dependLib.variant.contains(":growingio")
                || dependLib.variant.contains(":gio-sdk")
                || dependLib.variant.contains(":vds-observable-autoburry")
                || dependLib.variant.contains(":vds-observable-burry")
                || dependLib.variant.contains("com.growingio.giokit")
                || dependLib.variant.contains(":giokit")
            ) {
                gioConfig.gioSdks.add(dependLib)
                "add gio dependLib:${dependLib}".println()
            }
        }

        checkGiokitVersion(gioConfig.gioSdks)

        if (variant is ApplicationVariant) {
            gioConfig.domain = variant.applicationId
            "App PackageName is====>${gioConfig.domain}".println()

            //获取插件配置
            project.getAndroid<AppExtension>().let {
                val manifest = it.sourceSets.getAt("main").manifest.srcFile
                // 获取 AndroidManifest 下配置的 url scheme 如"growing.d80871b41ef40518"
                // 执行clean之后，无法再在 build 文件中找到 build/intermediates/merged_manifest/debug/AndroidManifest.xml，故无法找到 urlscheme
                // 第二次build时将会找到。。
                // 同样，如果只改变xml值未改变代码由于无法触发插件也不能实时更新
                manifest?.let{
                    gioConfig.xmlScheme = "empty"
                    val parser = SAXParserFactory.newInstance().newSAXParser()
                    val handler = GioKitXmlHandler {
                        gioConfig.xmlScheme = it
                    }
                    parser.parse(manifest, handler)
                    "App Application path====>${handler.applications}".println()
                    "App Activity path====>${handler.activities}".println()
                }
                val gioKit = project.extensions.getByType(GioKitExtension::class.java)
                gioConfig.setPluginExtension(gioKit)
            }
        } else {
            "variant isn't ApplicationVariant".println()
        }

        "processor->XmlScheme:${gioConfig.xmlScheme}".println()
        "processor->domain:${gioConfig.domain}".println()
        "processor->hasGioPlugin:${gioConfig.hasGioPlugin}".println()
        "processor->gioDepend:${gioConfig.gioSdks}".println()
    }

    fun checkGiokitVersion(set: MutableSet<DependLib>) {
        for (dependLib in set) {
            val depend = dependLib.variant
            if (depend.contains("giokit")) {
                val version = dependLib.version
                if (version.contains("project")) {
                    return
                }
                val pluginVersion = this.getGiokitPluginVersion()
                "version compare: ${version}<=>${pluginVersion}".println()
                if (version.equals(pluginVersion)) {
                    return
                } else {
                    throw Exception("Giokit plugin $pluginVersion is not equal Giokit library dependency $version!!")
                }
            }
        }
        throw Exception("Can't find Giokit library dependency!")
    }

    fun getGrowingioDepends() {
        for (configuration in project.configurations) {
            if ("releaseRuntimeClasspath" == configuration.getName()) {
                for (dependency in configuration.getIncoming().getResolutionResult().getRoot().getDependencies()) {
                    "releaseRuntimeClass: ${dependency.requested.displayName}".println()
                    val variants = dependency.requested.displayName.split(":")
                    if (variants.size == 3) {
                        dependLibs.add(DependLib(dependency.requested.displayName, variants[2]))
                    } else if (variants.size == 2) {
                        dependLibs.add(DependLib(dependency.requested.displayName, "project"))
                    }
                }
            }
        }
    }
}