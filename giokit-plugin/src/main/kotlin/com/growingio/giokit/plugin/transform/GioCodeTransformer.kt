package com.growingio.giokit.plugin.transform

import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.utils.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *     审查所有代码，获取调用埋点代码的位置
 * @author cpacm 2021/8/18
 */
class GioCodeTransformer() : ClassTransformer {

    override fun isTransformLatest(klass: ClassNode): Boolean {
        if (klass.className == "com.growingio.giokit.hook.GioTrackInfo") {
            klass.methods?.find { it.name == "initGioTrack" }
                .let {
                    return true
                }
        }
        return false
    }

    override fun transformLatest(context: TransformContext, klass: ClassNode): ClassNode {
        if (klass.className == "com.growingio.giokit.hook.GioTrackInfo") {
            klass.methods?.find { it.name == "initGioTrack" }
                .let { methodNode ->
                    methodNode?.instructions?.insert(createGioTrackInsnList())
                }
        }
        return klass
    }


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        with(GioConfigUtils.gioKitExt.trackFinder.domain) {
            if (this.isEmpty()) {
                when {
                    GioConfigUtils.defaultDomain.isNullOrEmpty() -> return klass
                    else -> add(GioConfigUtils.defaultDomain!!)
                }
            }
        }

        if (ignoreClassName(klass.className)) {
            return klass
        }

        klass.methods.forEach { methodNode ->
            var index = 1
            methodNode.instructions.iterator().asIterable()
                .filterIsInstance(MethodInsnNode::class.java).let { methodInsnNodes ->
                    methodInsnNodes.forEach { node ->
                        findTrackHook(
                            node.owner,
                            node.name,
                            klass.name,
                            methodNode.name,
                            index
                        )?.apply {
                            this.toString().println()
                            index += 1
                            GioConfigUtils.gioTracks.add(this)
                        }
                    }
                }
        }
        return klass
    }

    private fun createGioTrackInsnList(): InsnList {
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

            GioConfigUtils.gioTracks.forEach { track ->
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

    fun ignoreClassName(className: String): Boolean {
        for (domain in GioConfigUtils.gioKitExt.trackFinder.domain) {
            if (className.startsWith(domain, true)) {
                return false
            }
        }

        for (ignore in ignoreClassNames) {
            if (className.startsWith(ignore, true)) {
                return true
            }
        }
        return false
    }


    val ignoreClassNames = arrayListOf(
        "kotlin",
        "android",
        "com.growingio",
        "androidx",
        "com.google",
        "okhttp3",
        "okio",
        "com.github.ybq.android",
        "io.mattcarroll.hover",
        "org.intellij"
    )

//    fun findAutoTrackHook(
//        owner: String,
//        name: String,
//        globalClass: String,
//        globalMethod: String
//    ): GioTrackHook? {
//
//    }

    val trackHooks = arrayListOf(
        GioTrackHook("com.growingio.android.sdk.autotrack.CdpAutotracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.track.CdpTracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.autotrack.Autotracker", "trackCustomEvent"),
        GioTrackHook("com.growingio.android.sdk.track.Tracker", "trackCustomEvent"),
    )

    private fun findTrackHook(
        owner: String,
        name: String,
        globalClass: String,
        globalMethod: String,
        index: Int = 1
    ): GioTrackHook? {

        val customClassHook = GioConfigUtils.gioKitExt.trackFinder.className
        val customMethodHook = GioConfigUtils.gioKitExt.trackFinder.methodName
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