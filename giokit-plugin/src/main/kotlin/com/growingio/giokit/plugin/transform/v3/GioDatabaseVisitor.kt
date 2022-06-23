package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_V3_DELETE
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_V3_INSERT
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_V3_OVERDUE
import com.growingio.giokit.plugin.transform.HookInjectorClass.DB_V3_REMOVE
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
        return HookInjectorClass.DATABASE_V3_CLASSES
    }

    override fun generateAroundInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            DB_V3_INSERT -> createDbInsertInsnList(adviceAdapter)
        }
    }

    override fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            DB_V3_OVERDUE -> createDbOverdueInsnList(adviceAdapter)
            DB_V3_REMOVE -> createDbRemoveIdInsnList(adviceAdapter)
            DB_V3_DELETE -> createDbDeleteInsnList(adviceAdapter)
        }
    }

    private fun createDbInsertInsnList(adviceAdapter: AdviceAdapter) {
        "db insert succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "insertV3Event",
                "(Lcom/growingio/android/sdk/track/middleware/GEvent;)V",
                false
            )
        }
    }

    private fun createDbRemoveIdInsnList(adviceAdapter: AdviceAdapter) {
        "db removeId succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioDatabase.deleteEvent
            visitVarInsn(Opcodes.LLOAD, 2)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "deleteEvent",
                "(J)V",
                false
            )
        }
    }

    private fun createDbDeleteInsnList(adviceAdapter: AdviceAdapter) {
        "db delete succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioDatabase.deleteEvent
            visitVarInsn(Opcodes.LLOAD, 1)
            visitVarInsn(Opcodes.ALOAD, 4)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioDatabase",
                "removeEvents",
                "(JLjava/lang/String;)V",
                false
            )
        }
    }

    private fun createDbOverdueInsnList(adviceAdapter: AdviceAdapter) {
        "db overdue succeed:${context.className}".println()
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
}