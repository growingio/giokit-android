/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.giokit.plugin.transform.v3

import com.android.build.gradle.BaseExtension
import com.growingio.android.plugin.transform.AutoTrackerContext
import com.growingio.android.plugin.transform.GrowingBaseTransform
import com.growingio.android.plugin.utils.*
import com.growingio.giokit.plugin.utils.GioConfig
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.shouldClassModified
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2022/4/2
 */
internal class GiokitV3Transform(
    project: Project, android: BaseExtension,
    private val gioConfig: GioConfig
) : GrowingBaseTransform(project, android) {

    override fun getName() = "GiokitTransform"

    override fun transform(context: AutoTrackerContext, bytecode: ByteArray): ByteArray {
        try {
            val classReader = ClassReader(bytecode)
            if (!shouldClassModified(
                    normalize(classReader.className)
                )
            ) { return bytecode }


            val gioKitWriter = object : ClassWriter(classReader, COMPUTE_MAXS) {
                fun getApi(): Int {
                    return api
                }
            }
            val apiVersion = gioKitWriter.getApi()

            val classContextCompat = object : GioTransformContext {
                override val className = normalize(classReader.className)
                override fun isAssignable(subClazz: String, superClazz: String): Boolean {
                    return context.klassPool.get(superClazz).isAssignableFrom(subClazz)
                }

                override val gioConfig: GioConfig get() = this@GiokitV3Transform.gioConfig

                override val generatedDir: File
                    get() = File(project.buildDir, "generated/source/buildConfig/${context.name}/")
            }


            val databaseVisitor = GioDatabaseVisitor(apiVersion, gioKitWriter, classContextCompat)
            val httpVisitor = GioHttpVisitor(apiVersion, databaseVisitor, classContextCompat)
            val webviewVisitor = GioWebViewVisitor(apiVersion, httpVisitor, classContextCompat)
            val configVisitor = GioConfigVisitor(apiVersion, webviewVisitor, classContextCompat)
            val codeVisitor = GioCodeVisitor(apiVersion, configVisitor, classContextCompat)
            classReader.accept(codeVisitor, ClassReader.EXPAND_FRAMES)
            return gioKitWriter.toByteArray()
        } catch (t: Throwable) {
            e(
                "Unfortunately, an error has occurred while processing " + context.name + ". Please copy your build logs and the jar containing this class and visit https://www.growingio.com, thanks!\n",
                t
            )
        }
        return bytecode
    }
}