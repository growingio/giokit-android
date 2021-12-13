package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.base.asIterable
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.className
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *
 * @author cpacm 2021/8/23
 */
class GioDatabaseTransformer : ClassTransformer {

    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        //适配3.3.3之前版本
        if (className == "com.growingio.android.sdk.track.middleware.EventsSQLite") {
            klass.methods.find { it.name == "insertEvent" }.let { methodNode ->
                methodNode?.apply { insertOldEvent(this) }
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

        //3.3.3版本之后
        if (className == "com.growingio.database.EventDataManager") {
            klass.methods.find { it.name == "insertEvent" }.let { methodNode ->
                methodNode?.apply { insertGEvent(this) }
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
            add(VarInsnNode(Opcodes.ALOAD, 4))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "removeEvents",
                    "(JLjava/lang/String;)V",
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

    //INVOKEVIRTUAL android/content/ContentResolver.insert (Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    private fun insertGEvent(methodNode: MethodNode, hasReturn: Boolean = true) {
        methodNode.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
            ?.filter { method ->
                method.opcode == Opcodes.INVOKEVIRTUAL
                        && method.owner == "android/content/ContentResolver"
                        && method.name == "insert"
            }?.forEach { fieldInsnNode ->
                val preNode = fieldInsnNode.previous
                val index = if (preNode is VarInsnNode) preNode.`var` + 1 else 6
                methodNode.instructions.insert(fieldInsnNode, createDbInsertInsnList(index))
                return
            }
    }

    private fun insertOldEvent(methodNode: MethodNode) {
        methodNode.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
            ?.filter { method ->
                method.opcode == Opcodes.INVOKEVIRTUAL
                        && method.owner == "android/content/ContentResolver"
                        && method.name == "insert"
            }?.forEach { fieldInsnNode ->
                // remove pop()
                methodNode.instructions.remove(fieldInsnNode.next)
                methodNode.instructions.insert(fieldInsnNode, with(InsnList()) {
                    add(VarInsnNode(Opcodes.ALOAD, 1))
                    add(
                        MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "com/growingio/giokit/hook/GioDatabase",
                            "insertV3Event",
                            "(Landroid/net/Uri;Lcom/growingio/android/sdk/track/middleware/GEvent;)V",
                            false
                        )
                    )
                    this
                })
                return
            }
    }

    //    astore 6
    //    aload 1
    //    aload 6
    //    INVOKESTATIC com/growingio/database/GioDatabase.insert (Lcom/growingio/android/sdk/track/middleware/GEvent;Landroid/net/Uri;)V
    //    aload 6  //return
    private fun createDbInsertInsnList(index: Int): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ASTORE, index))
            add(VarInsnNode(Opcodes.ALOAD, index))
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "insertV3Event",
                    "(Landroid/net/Uri;Lcom/growingio/android/sdk/track/middleware/GEvent;)V",
                    false
                )
            )
            add(VarInsnNode(Opcodes.ALOAD, index))
            this
        }
    }

}