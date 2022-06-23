package com.growingio.giokit.plugin.transform.saas

import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_SAAS_CLEANUP
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_SAAS_CLEAR
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_SAAS_SAVE
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * <p>
 *
 * @author cpacm 2022/6/16
 */
class GioDatabaseVisitor(api: Int, ncv: ClassVisitor, context: GioTransformContext) :
    GioBaseClassVisitor(api, ncv, context) {


    override fun getTargetHookData(): List<HookInjectorClass.HookData> {
        return HookInjectorClass.DATABASE_SAAS_CLASSES
    }

    override fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            DB_SAAS_SAVE -> createDbInsertInsnList(adviceAdapter)
            DB_SAAS_CLEANUP -> createDbOverdueInsnList(adviceAdapter)
            DB_SAAS_CLEAR -> createDbRemovesInsnList(adviceAdapter)
        }
    }

    private fun createDbInsertInsnList(adviceAdapter: AdviceAdapter) {
        "saas db insert succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioDatabase.insertEvent
            visitVarInsn(Opcodes.ALOAD, 3)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "insertSaasEvent",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }

    private fun createDbOverdueInsnList(adviceAdapter: AdviceAdapter) {
        "saas db overdue succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioDatabase.outdatedEvents
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "outdatedEvents",
                "()V",
                false
            )
        }
    }

    private fun createDbRemovesInsnList(adviceAdapter: AdviceAdapter) {
        "db delete succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioDatabase.deleteEvent
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/growingio/android/sdk/data/db/MessageUploader\$UPLOAD_TYPE",
                "toString",
                "()Ljava/lang/String;",
                false
            )
            visitVarInsn(Opcodes.ALOAD, 2)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "removeSaasEvents",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                false
            )
        }
    }
}