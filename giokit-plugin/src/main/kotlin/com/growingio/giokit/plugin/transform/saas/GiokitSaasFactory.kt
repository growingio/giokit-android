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

package com.growingio.giokit.plugin.transform.saas

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.growingio.android.plugin.utils.normalize
import com.growingio.giokit.plugin.utils.GioConfig
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.shouldClassModified
import org.gradle.api.tasks.Internal
import org.gradle.api.provider.Property
import org.objectweb.asm.ClassVisitor
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2022/4/6
 */
internal abstract class GiokitSaasFactory :
    AsmClassVisitorFactory<GiokitParams> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        val classContextCompat = object : GioTransformContext {
            override val className = classContext.currentClassData.className

            override fun isAssignable(subClazz: String, superClazz: String): Boolean {
                return classContext.loadClassData(normalize(subClazz))?.let {
                    it.className == normalize(superClazz) ||
                            it.superClasses.indexOf(normalize(superClazz)) >= 0 ||
                            it.interfaces.indexOf(normalize(superClazz)) >= 0
                } ?: false
            }

            override fun classIncluded(clazz: String): Boolean {
                return true
            }

            override val gioConfig: GioConfig get() = parameters.get().gioConfig.get()
            override val generatedDir: File get() = parameters.get().additionalClassesDir.get()
        }
        val apiVersion = instrumentationContext.apiVersion.get()

        val databaseVisitor = GioDatabaseVisitor(apiVersion, nextClassVisitor, classContextCompat)
        val httpVisitor = GioHttpVisitor(apiVersion, databaseVisitor, classContextCompat)
        val webviewVisitor = GioWebViewVisitor(apiVersion, httpVisitor, classContextCompat)
        val configVisitor = GioConfigVisitor(apiVersion, webviewVisitor, classContextCompat)
        val codeVisitor = GioCodeVisitor(apiVersion, configVisitor, classContextCompat)
        return codeVisitor
    }


    override fun isInstrumentable(classData: ClassData): Boolean {
        return shouldClassModified(
            classData.className
        )
    }
}


internal interface GiokitParams : InstrumentationParameters {
    @get:Internal
    val additionalClassesDir: Property<File>

    @get:Internal
    val gioConfig: Property<GioConfig>
//
//    @get:Internal
//    val includePackages: Property<Array<String>>

}
