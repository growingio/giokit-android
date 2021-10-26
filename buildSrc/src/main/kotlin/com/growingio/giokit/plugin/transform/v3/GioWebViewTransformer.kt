package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.transform.saas.GioWebViewTransformer
import com.growingio.giokit.plugin.utils.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GioWebViewTransformer : ClassTransformer {

    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        //直接在webview 声明的chromeclient注入js，若没有声明则无法注入
        if (klass.className.ignoreClass(context)) {
            return klass
        }
        // find origin webview
        if (injectWebView(context, klass, GioWebViewTransformer.WEBVIEW_INJECTOR)) return klass

        // find x5 webview
        if (injectWebView(context, klass, GioWebViewTransformer.X5_INJECTOR)) return klass

        // find uc webview
        if (injectWebView(context, klass, GioWebViewTransformer.UC_INJECTOR)) return klass

        return klass
    }

    private fun injectWebView(
        context: GioTransformContext,
        klass: ClassNode,
        injector: Array<String>
    ): Boolean {
        val className = klass.className
        val targetClass = injector[0]
        val webClass = loadedClass[targetClass].run {
            if (this == null) {
                targetClass.loadClass(context)?.also {
                    loadedClass[targetClass] = it
                }
            }
            loadedClass[targetClass]
        }
        if (webClass != null && className.isAssignableFrom(context, webClass)) {
            klass.methods.find { it.name == "onProgressChanged" }.let { methodNode ->
                if (methodNode == null) {
                    generateProgressMethod(klass, injector)
                } else {
                    methodNode.instructions?.insert(createWebHookInsnList(injector))
                }
            }
            return true
        }
        return false
    }

    private val loadedClass = hashMapOf<String, Class<*>>()

    private fun generateProgressMethod(klass: ClassNode, injector: Array<String>) {
        val mn = MethodNode(
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
            "onProgressChanged",
            "(${injector[1]}I)V",
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
                    "(${injector[1]}I)V",
                    false
                )
            )
            this
        }
        mn.instructions?.insert(createWebHookInsnList(injector))
        mn.instructions?.insert(superList)

        mn.visitInsn(Opcodes.RETURN)
        mn.maxStack = 3
        mn.maxLocals = 3
        mn.visitEnd()
        klass.methods.add(mn)
    }

    private fun createWebHookInsnList(injector: Array<String>): InsnList {
        return with(InsnList()) {
            //调用 GioWebView addCircleJsToWebView
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(VarInsnNode(Opcodes.ILOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioWebView",
                    injector[2],
                    "(${injector[1]}I)V",
                    false
                )
            )
            this
        }
    }


    companion object {
        val WEBVIEW_INJECTOR = arrayOf(
            "android.webkit.WebChromeClient",
            "Landroid/webkit/WebView;",
            "addCircleJsToWebView",
            "SystemWebView"
        )
        val X5_INJECTOR = arrayOf(
            "com.tencent.smtt.sdk.WebChromeClient",
            "Lcom/tencent/smtt/sdk/WebView;",
            "addCircleJsToX5",
            "X5WebView",
        )
        val UC_INJECTOR = arrayOf(
            "com.uc.webview.export.WebChromeClient",
            "Lcom/uc/webview/export/WebView;",
            "addCircleJsToUc",
            "UCWebView",
        )
    }
}