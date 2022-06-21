package com.growingio.giokit.plugin.transform

import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * <p>
 *
 * @author cpacm 2022/6/16
 */
abstract class GioBaseClassVisitor(api: Int, ncv: ClassVisitor, val context: GioTransformContext) :
    ClassVisitor(api, ncv) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    open fun isInjectClass(hookData: HookInjectorClass.HookData): Boolean {
        return context.className == hookData.targetClassName
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        getTargetHookData().filter {
            isInjectClass(it) && name == it.targetMethodName && (it.targetMethodDesc?.let { it == descriptor } ?: true)
        }.forEach {
            "visitMethod:${context.className}-${name}".println()
            val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            return InjectMethodVisitor(api, mv, access, name, descriptor, it)
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    inner class InjectMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        val hookData: HookInjectorClass.HookData
    ) : AdviceAdapter(api, nmv, access, name, descriptor) {

        private var hasGenerateMethod = false

        override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
            if (hookData.hookAroundData != null &&
                hookData.hookAroundData.owner == owner &&
                hookData.hookAroundData.name == name &&
                hookData.hookAroundData.descriptor == descriptor &&
                hookData.hookAroundData.opcode == opcode &&
                !hasGenerateMethod
            ) {
                hasGenerateMethod = true
                "AroundFieldHook:${opcode},${owner},$name,$descriptor".println()
                generateAroundInsn(hookData.hookName, this)
            }
            super.visitFieldInsn(opcode, owner, name, descriptor)
        }

        override fun visitMethodInsn(
            opcode: Int,
            owner: String?,
            name: String?,
            descriptor: String?,
            isInterface: Boolean
        ) {
            if (!hookData.isAfter && hookData.hookAroundData != null &&
                hookData.hookAroundData.owner == owner &&
                hookData.hookAroundData.name == name &&
                hookData.hookAroundData.descriptor == descriptor &&
                hookData.hookAroundData.opcode == opcode &&
                !hasGenerateMethod
            ) {
                hasGenerateMethod = true
                "AroundMethodHook:${opcode},${owner},$name,$descriptor".println()
                generateAroundInsn(hookData.hookName, this)
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            if (hookData.isAfter && hookData.hookAroundData != null &&
                hookData.hookAroundData.owner == owner &&
                hookData.hookAroundData.name == name &&
                hookData.hookAroundData.descriptor == descriptor &&
                hookData.hookAroundData.opcode == opcode &&
                !hasGenerateMethod
            ) {
                hasGenerateMethod = true
                "AroundMethodHook:${opcode},${owner},$name,$descriptor".println()
                generateAroundInsn(hookData.hookName, this)
            }
        }

        override fun onMethodEnter() {
            if (!hookData.isAfter && hookData.hookAroundData == null && !hasGenerateMethod) {
                hasGenerateMethod = true
                "InjectHook Before:$name".println()
                generateMethodInsn(hookData.hookName, this)
            }
            super.onMethodEnter()
        }

        override fun onMethodExit(opcode: Int) {
            if (hookData.isAfter && hookData.hookAroundData == null && !hasGenerateMethod) {
                hasGenerateMethod = true
                "InjectHook After:$name".println()
                generateMethodInsn(hookData.hookName, this)
            }
            super.onMethodExit(opcode)
        }
    }

    abstract fun getTargetHookData(): List<HookInjectorClass.HookData>

    open fun generateAroundInsn(name: String, adviceAdapter: AdviceAdapter) {}
    open fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {}

}