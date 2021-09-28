package com.growingio.giokit.plugin.transform.v3

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.transform.ClassTransformer
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
class GioInjectTransformer : ClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className
        if (className == "com.growingio.giokit.hook.GioPluginConfig") {
            klass.methods.find { it.name == "initGioKitConfig" }
                .let { methodNode ->
                    methodNode?.instructions?.insert(createPluginConfigInsnList())
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

    private fun createPluginConfigInsnList(): InsnList {
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
            add(InsnNode(if (GioConfigUtils.hasGioPluginV3) Opcodes.ICONST_1 else Opcodes.ICONST_0))
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
            add(LdcInsnNode(GioConfigUtils.v3XmlScheme))
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
            add(LdcInsnNode(GioConfigUtils.getGioDepend(GioConfigUtils.gioV3Sdks)))
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

            //put("isSaasSdk",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("isSaasSdk"))
            add(InsnNode(Opcodes.ICONST_0))
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