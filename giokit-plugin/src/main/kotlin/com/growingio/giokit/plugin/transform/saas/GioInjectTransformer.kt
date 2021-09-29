package com.growingio.giokit.plugin.transform.saas

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.className
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GioInjectTransformer : ClassTransformer {

    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        val className = klass.className
        if (className == "com.growingio.giokit.hook.GioPluginConfig") {
            klass.methods.find { it.name == "initGioKitConfig" }
                .let { methodNode ->
                    methodNode?.instructions?.insert(createPluginConfigInsnList(context))
                }
            return klass
        }
        if (isAssignable(context, className)) {
            className.println()
            klass.methods.find { it.name == "onCreate" }.let { method ->
                //插入代码到return前
                var node = method?.instructions?.last
                while (node != null) {
                    if (node.opcode == Opcodes.RETURN) {
                        method?.instructions?.insertBefore(node, addApplicationTailInsnList())
                        break
                    }
                    node = node.previous
                }
            }
            return klass
        }

        return klass
    }

    private fun addApplicationTailInsnList(): InsnList {
        return with(InsnList()) {
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioPluginConfig",
                    "checkSdkHasInit",
                    "()V",
                    false
                )
            )
            this
        }
    }

    private fun createPluginConfigInsnList(context: GioTransformContext): InsnList {
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(Opcodes.NEW, "java/util/HashMap"))
            add(InsnNode(Opcodes.DUP))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(Opcodes.ASTORE, 0))
            //put("hasGioPlugin",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("gioPlugin"))
            add(InsnNode(if (context.gioConfig.hasGioPlugin) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("xmlScheme","")
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("xmlScheme"))
            add(LdcInsnNode(context.gioConfig.xmlScheme))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("gioDepend","")
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("gioDepend"))
            add(LdcInsnNode(context.gioConfig.getGioDepend(context.gioConfig.gioSdks)))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("isSaasSdk",false)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("isSaasSdk"))
            add(InsnNode(Opcodes.ICONST_1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //将配置放入GioPluginConfig中 com.growingio.giokit.hook.GioPluginConfig
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioPluginConfig",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }
    }

    private fun isAssignable(context: TransformContext, className: String): Boolean {
        try {
            val targetClass =
                context.klassPool.classLoader.loadClass("android.app.Application")
            val findClass = context.klassPool.classLoader.loadClass(className)
            return targetClass.isAssignableFrom(findClass)
        } catch (e: ClassNotFoundException) {
        } catch (e: NoClassDefFoundError) {
        }
        return false
    }
}