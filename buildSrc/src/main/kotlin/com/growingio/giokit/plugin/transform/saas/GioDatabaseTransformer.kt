package com.growingio.giokit.plugin.transform.saas

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.className
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
        if (className == "com.growingio.android.sdk.data.db.DBAdapter") {
            klass.methods.find { it.name == "saveEvent" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbInsertInsnList())
            }
            klass.methods.find { it.name == "cleanupEvents" }.let { methodNode ->
                if (methodNode?.parameters?.size == 1) {
                    methodNode.instructions?.insert(createDbOverdueInsnList())
                }
            }
            klass.methods.find { it.name == "cleanDataString" }.let { methodNode ->
                methodNode?.instructions?.insert(createDbRemovesInsnList())
            }
        }
        return klass
    }

    private fun createDbRemovesInsnList(): InsnList {
        //long cleanDataString(MessageUploader.UPLOAD_TYPE type, String lastId)
        return with(InsnList()) {
            //调用 GioDatabase.deleteEvent
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/growingio/android/sdk/data/db/MessageUploader\$UPLOAD_TYPE", "toString", "()Ljava/lang/String;", false))
            add(VarInsnNode(Opcodes.ALOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "removeSaasEvents",
                    "(Ljava/lang/String;Ljava/lang/String;)V",
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
        //public void saveEvent(String type, boolean instant, String json)
        return with(InsnList()) {
            //调用 GioDatabase.insertEvent
            add(VarInsnNode(Opcodes.ALOAD, 3))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioDatabase",
                    "insertSaasEvent",
                    "(Ljava/lang/String;)V",
                    false
                )
            )
            this
        }
    }
}