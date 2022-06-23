package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.APPLICATION_START
import com.growingio.giokit.plugin.transform.HookInjectorClass.CONFIG_INJECT
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
class GioConfigVisitor(api: Int, ncv: ClassVisitor, context: GioTransformContext) :
    GioBaseClassVisitor(api, ncv, context) {


    override fun getTargetHookData(): List<HookInjectorClass.HookData> {
        return HookInjectorClass.CONFIG_CLASSES
    }

    override fun isInjectClass(hookData: HookInjectorClass.HookData): Boolean {
        if (hookData.targetClassName == "android.app.Application") {
            if (context.isAssignable(context.className, hookData.targetClassName)) return true
            return false
        }
        return super.isInjectClass(hookData)
    }

    override fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            APPLICATION_START -> addApplicationTailInsnList(adviceAdapter)
            CONFIG_INJECT -> addPluginConfigInsnList(adviceAdapter)
        }
    }

    private fun addApplicationTailInsnList(adviceAdapter: AdviceAdapter) {
        "hook application succeed:${context.className}".println()
        adviceAdapter.apply {
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioPluginConfig",
                "checkSdkHasInit",
                "()V",
                false
            )
        }
    }

    private fun addPluginConfigInsnList(adviceAdapter: AdviceAdapter) {
        "insert application succeed:${context.className}".println()
        adviceAdapter.apply {
            //new HashMap
            visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
            visitInsn(Opcodes.DUP)
            visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false)
            //保存变量
            visitVarInsn(Opcodes.ASTORE, 0)
            //put("hasGioPlugin",true)
            visitVarInsn(Opcodes.ALOAD, 0)
            visitLdcInsn("gioPlugin")
            visitInsn(if (context.gioConfig.hasGioPlugin) Opcodes.ICONST_1 else Opcodes.ICONST_0)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/Boolean",
                "valueOf",
                "(Z)Ljava/lang/Boolean;",
                false
            )
            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/Map",
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                true
            )

            visitInsn(Opcodes.POP)

            //put("xmlScheme","")
            visitVarInsn(Opcodes.ALOAD, 0)
            visitLdcInsn("xmlScheme")
            visitLdcInsn(context.gioConfig.xmlScheme)
            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/Map",
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false

            )
            visitInsn(Opcodes.POP)

            //put("gioDepend","")
            visitVarInsn(Opcodes.ALOAD, 0)
            visitLdcInsn("gioDepend")
            visitLdcInsn(context.gioConfig.getGioDepend(context.gioConfig.gioSdks))
            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/Map",
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false
            )
            visitInsn(Opcodes.POP)

            //put("isSaasSdk",true)
            visitVarInsn(Opcodes.ALOAD, 0)
            visitLdcInsn("isSaasSdk")
            visitInsn(Opcodes.ICONST_0)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/Boolean",
                "valueOf",
                "(Z)Ljava/lang/Boolean;",
                false
            )

            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/Map",
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                true
            )
            visitInsn(Opcodes.POP)

            //将配置放入GioPluginConfig中 com.growingio.giokit.hook.GioPluginConfig
            visitVarInsn(Opcodes.ALOAD, 0)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioPluginConfig",
                "inject",
                "(Ljava/util/Map;)V",
                false

            )
        }
    }
}