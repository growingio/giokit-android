package com.growingio.giokit.plugin.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.TransformListener
import org.objectweb.asm.tree.ClassNode
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2021/8/18
 */
interface ClassTransformer : TransformListener {

    val name: String
        get() = javaClass.simpleName

    fun getReportDir(context: TransformContext): File =
        File(File(context.reportsDir, name), context.name)

    fun getReport(context: TransformContext, name: String): File {
        val report: File by lazy {
            val dir = getReportDir(context)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, name)
            if (!file.exists()) {
                file.createNewFile()
            }
            file
        }
        return report
    }

    /**
     * Transform the specified class node
     *
     * @param context The transform context
     * @param klass The class node to be transformed
     * @return The transformed class node
     */
    fun transform(context: TransformContext, klass: ClassNode) = klass

    fun transformLatest(context: TransformContext, klass: ClassNode): ClassNode {
        return klass
    }

    fun isTransformLatest(klass: ClassNode) = false
}