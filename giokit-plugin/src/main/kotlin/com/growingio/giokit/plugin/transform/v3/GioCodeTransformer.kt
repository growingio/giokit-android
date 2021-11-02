package com.growingio.giokit.plugin.transform.v3

import com.didiglobal.booster.kotlinx.asIterable
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *     审查所有代码，获取调用埋点代码的位置
 * @author cpacm 2021/8/18
 */
class GioCodeTransformer : ClassTransformer {

    override fun isTransformLatest(klass: ClassNode): Boolean {
        if (klass.className == "com.growingio.giokit.hook.GioTrackInfo") {
            klass.methods?.find { it.name == "initGioTrack" }
                .let {
                    return true
                }
        }
        return false
    }

    override fun transformLatest(context: GioTransformContext, klass: ClassNode): ClassNode {
        if (klass.className == "com.growingio.giokit.hook.GioTrackInfo") {
            klass.methods?.find { it.name == "initGioTrack" }
                .let { methodNode ->
                    "GioCodeTransformer Latest".println()
                    context.gioConfig.gioTracks.forEach {
                        it.toString().println()
                    }
                    methodNode?.instructions?.insert(createGioTrackInsnList(context))
                }
        }
        return klass
    }


    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        with(context.gioConfig.gioKitExt.trackFinder.domain) {
            if (this.isEmpty()) {
                when {
                    context.gioConfig.domain.isEmpty() -> return klass
                    else -> add(context.gioConfig.domain)
                }
            }
        }

        if (klass.className.ignoreClass(context)) {
            return klass
        }

        klass.methods.forEach { methodNode ->
            var index = 1
            methodNode.instructions.iterator().asIterable()
                .filterIsInstance(MethodInsnNode::class.java).let { methodInsnNodes ->
                    methodInsnNodes.forEach { node ->
                        findTrackHook(
                            context,
                            node.owner,
                            node.name,
                            klass.name,
                            methodNode.name,
                            index
                        )?.apply {
                            this.toString().println()
                            index += 1
                            context.gioConfig.gioTracks.add(this)
                        }
                    }
                }
        }
        return klass
    }

    private fun createGioTrackInsnList(context: GioTransformContext): InsnList {
        return with(InsnList()) {
            add(TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"))
            add(InsnNode(Opcodes.DUP))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "java/util/ArrayList",
                    "<init>",
                    "()V",
                    false
                )
            )
            //保存变量
            add(VarInsnNode(Opcodes.ASTORE, 0))

            context.gioConfig.gioTracks.forEach { track ->
                add(VarInsnNode(Opcodes.ALOAD, 0))
                add(LdcInsnNode(track.className + "::" + track.methodName))

                add(
                    MethodInsnNode(
                        Opcodes.INVOKEINTERFACE,
                        "java/util/List",
                        "add",
                        "(Ljava/lang/Object;)Z",
                        false
                    )
                )
                add(InsnNode(Opcodes.POP))
            }

            //将List注入到GioTrackInfo中
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioTrackInfo",
                    "inject",
                    "(Ljava/util/List;)V",
                    false
                )
            )
            this
        }
    }

    val trackHooks = arrayListOf(
        GioTrackHook("com.growingio.android.sdk.autotrack.CdpAutotracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.track.CdpTracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.autotrack.Autotracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.track.Tracker", "trackCustomEvent"),
    )

    private fun findTrackHook(
        context: GioTransformContext,
        owner: String,
        name: String,
        globalClass: String,
        globalMethod: String,
        index: Int = 1
    ): GioTrackHook? {

        val customClassHook = context.gioConfig.gioKitExt.trackFinder.className
        val customMethodHook = context.gioConfig.gioKitExt.trackFinder.methodName
        if (owner.formatDot == customClassHook && name.formatDot == customMethodHook) {
            return GioTrackHook(globalClass, globalMethod.let {
                if (index > 1) "$it-$index"
                else it
            }, owner, name)
        }

        repeat(trackHooks.filter { hook -> hook.className == owner.formatDot && hook.methodName == name.formatDot }.size) {
            return GioTrackHook(globalClass, globalMethod.let {
                if (index > 1) it + index.toString()
                else it
            }, owner, name)
        }

        return null

    }


}