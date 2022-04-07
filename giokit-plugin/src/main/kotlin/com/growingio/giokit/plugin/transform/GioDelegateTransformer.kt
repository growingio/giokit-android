package com.growingio.giokit.plugin.transform

import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.GioTransformListener
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

/**
 * <p>
 *
 * 多个作用：1. 简化classReader 调用；2.统计每个transform花费的cpu时间;3.双重transform
 * @author cpacm 2021/8/18
 */
class GioDelegateTransformer(
    internal val transformers: Iterable<ClassTransformer>
) : GioTransformListener {

    private val threadMxBean = ManagementFactory.getThreadMXBean()

    private val durations = mutableMapOf<ClassTransformer, Long>()

    override fun onPreTransform(context: GioTransformContext) {
        this.transformers.forEach { transformer ->
            this.threadMxBean.sumCpuTime(transformer) {
                transformer.onPreTransform(context)
            }
        }
    }

    override fun onPostTransform(context: GioTransformContext) {
        this.transformers.forEach { transformer ->
            this.threadMxBean.sumCpuTime(transformer) {
                transformer.onPostTransform(context)
            }
        }

        val w1 = this.durations.keys.map {
            it.javaClass.name.length
        }.max() ?: 20
        this.durations.forEach { (transformer, ns) ->
            println("${transformer.javaClass.name.padEnd(w1 + 1)}: ${ns / 1000000} ms")
        }
    }

    fun transform(
        context: GioTransformContext,
        bytecode: ByteArray,
        hasLatest: (Boolean) -> Unit
    ): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            this.transformers.fold(ClassNode().also { klass ->
                ClassReader(bytecode).accept(klass, ClassReader.EXPAND_FRAMES)
            }) { klass, transformer ->
                this.threadMxBean.sumCpuTime(transformer) {
                    if (transformer.isTransformLatest(klass)) {
                        hasLatest(true)
                    }
                    transformer.transform(context, klass)
                }
            }.accept(writer)
        }.toByteArray()
    }

    fun transformLatest(context: GioTransformContext, bytecode: ByteArray): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            this.transformers.fold(ClassNode().also { klass ->
                ClassReader(bytecode).accept(klass, ClassReader.EXPAND_FRAMES)
            }) { klass, transformer ->
                this.threadMxBean.sumCpuTime(transformer) {
                    if (transformer.isTransformLatest(klass)) {
                        transformer.transformLatest(context, klass)
                    } else {
                        transformer.transform(context, klass)
                    }
                }
            }.accept(writer)
        }.toByteArray()
    }

    private fun <R> ThreadMXBean.sumCpuTime(transformer: ClassTransformer, action: () -> R): R {
        val ct0 = this.currentThreadCpuTime
        val result = action()
        val ct1 = this.currentThreadCpuTime
        durations[transformer] = durations.getOrDefault(transformer, 0) + (ct1 - ct0)
        return result
    }
}