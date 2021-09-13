package com.growingio.giokit.plugin.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.didiglobal.booster.gradle.SCOPE_FULL_PROJECT
import com.didiglobal.booster.gradle.SCOPE_FULL_WITH_FEATURES
import com.didiglobal.booster.gradle.SCOPE_PROJECT
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.transform.AbstractKlassPool
import com.didiglobal.booster.transform.Klass
import com.google.common.collect.ImmutableSet
import com.growingio.giokit.plugin.utils.println
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/8/18
 */
open class GioKitBaseTransform(val project: Project) : Transform() {

    private val android: BaseExtension = project.getAndroid()

    private lateinit var androidKlassPool: AbstractKlassPool

    init {
        project.afterEvaluate {
            androidKlassPool = object : AbstractKlassPool(android.bootClasspath) {}
        }
    }

    val bootKlassPool: AbstractKlassPool
        get() = androidKlassPool

    internal open val transformers = listOf<GioDelegateTransformer>()

    override fun getName() = this.javaClass.simpleName

    override fun getInputTypes() = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = when {
        transformers.isEmpty() -> mutableSetOf()
        project.plugins.hasPlugin("con.android.library") -> SCOPE_PROJECT
        project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
        project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
        else -> mutableSetOf()
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> = when {
        transformers.isEmpty() -> when {
            project.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
            project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
            project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
            else -> ImmutableSet.of()
        }
        else -> super.getReferencedScopes()
    }

    override fun isIncremental() = true

    override fun transform(transformInvocation: TransformInvocation) {
        GioKitTransformInvocation(transformInvocation, this).apply {
            if (isIncremental) {
                doIncrementalTransform()
            } else {
                outputProvider?.deleteAll()
                doFullTransform()
            }
            //赋值代码信息时需要再走一遍transform
            doLatestTransform()
        }
    }


}