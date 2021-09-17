package com.growingio.giokit.plugin.transform

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.utils.GioConfigUtils
import com.growingio.giokit.plugin.utils.className
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *
 * @author cpacm 2021/8/23
 */
class GioDatabaseTransformer : ClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className
        if (className == "com.growingio.android.sdk.track.middleware.EventsSQLite") {
            klass.methods.find { it.name == "insertEvent" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbInsertInsnList())
            }
            klass.methods.find { it.name == "removeOverdueEvents" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbOverdueInsnList())
            }
            klass.methods.find { it.name == "removeEventById" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbRemoveIdInsnList())
            }
            klass.methods.find { it.name == "removeEvents" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbRemovesInsnList())
            }


        }
        return klass
    }

    private fun createDbRemovesInsnList(): InsnList {
        return with(InsnList()) {
            //调用 GioDatabase.deleteEvent
            add(VarInsnNode(Opcodes.LLOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "removeEvents",
                    "(J)V",
                    false
                )
            )
            this
        }
    }

    private fun createDbRemoveIdInsnList(): InsnList {
        return with(InsnList()) {
            //调用 GioDatabase.deleteEvent
            add(VarInsnNode(Opcodes.LLOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "deleteEvent",
                    "(J)V",
                    false
                )
            )
            this
        }
    }

    private fun createDbOverdueInsnList(): InsnList {
        return with(InsnList()) {
            //调用 GioDatabase.outdatedEvents
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "outdatedEvents",
                    "()V",
                    false
                )
            )
            this
        }
    }

    private fun createDbInsertInsnList(): InsnList {
        return with(InsnList()) {
            //调用 GioDatabase.insertEvent
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "insertEvent",
                    "(Lcom/growingio/android/sdk/track/middleware/GEvent;)V",
                    false
                )
            )
            this
        }
    }
}