package com.growingio.giokit.plugin.transform.v3

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.className
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GioWebViewTransformer : ClassTransformer {

    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        val className = klass.className
        if (ignoreClassName(context, klass.className)) {
            return klass
        }
        if (!isAssignable(context, className)) {
            return klass
        }
        klass.methods.find { it.name == "onProgressChanged" }.let { methodNode ->
            if (methodNode == null) {
                generateProgressMethod(klass)
            } else {
                methodNode.instructions?.insert(createWebHookInsnList())
            }
        }
        return klass
    }

    private fun generateProgressMethod(klass: ClassNode) {
        val mn = MethodNode(
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
            "onProgressChanged",
            "(Landroid/webkit/WebView;I)V",
            null,
            null
        )

        val superList = with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(VarInsnNode(Opcodes.ILOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    klass.superName,
                    "onProgressChanged",
                    "(Landroid/webkit/WebView;I)V",
                    false
                )
            )
            this
        }

        mn.instructions?.insert(createWebHookInsnList())
        mn.instructions?.insert(superList)

        mn.visitInsn(Opcodes.RETURN)
        mn.maxStack = 3
        mn.maxLocals = 3
        mn.visitEnd()
        klass.methods.add(mn)

    }

    private fun isAssignable(context: TransformContext, className: String): Boolean {
        try {
            val targetClass =
                context.klassPool.classLoader.loadClass("android.webkit.WebChromeClient")
            val findClass = context.klassPool.classLoader.loadClass(className)
            return targetClass.isAssignableFrom(findClass)
        } catch (e: ClassNotFoundException) {
        } catch (e: NoClassDefFoundError) {
        }
        return false
    }

    private fun createWebHookInsnList(): InsnList {
        return with(InsnList()) {
            //调用 GioWebView addCircleJsToWebView
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(VarInsnNode(Opcodes.ILOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioWebView",
                    "addCircleJsToWebView",
                    "(Landroid/webkit/WebView;I)V",
                    false
                )
            )
            this
        }
    }

    fun ignoreClassName(context: GioTransformContext, className: String): Boolean {
        for (domain in context.gioConfig.gioKitExt.trackFinder.domain) {
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
}