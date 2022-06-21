package com.growingio.giokit.plugin.transform.v3

import com.growingio.android.plugin.utils.unNormalize
import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.WEBVIEW_SYSTEM
import com.growingio.giokit.plugin.transform.HookInjectorClass.WEBVIEW_UC
import com.growingio.giokit.plugin.transform.HookInjectorClass.WEBVIEW_X5
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

/**
 * <p>
 *
 * @author cpacm 2022/6/16
 */
class GioWebViewVisitor(api: Int, ncv: ClassVisitor, context: GioTransformContext) :
    GioBaseClassVisitor(api, ncv, context) {

    private var generateHookData: HookInjectorClass.HookData? = null

    override fun isInjectClass(hookData: HookInjectorClass.HookData): Boolean {
        if (context.isAssignable(context.className, hookData.targetClassName)) {
            generateHookData = hookData
            return true
        }
        return false
    }

    override fun visitEnd() {
        // if generateHookData is not null,we should generate new method
        if (generateHookData != null) {
            val tempData = generateHookData
            "generate method onProgressChanged() success".println()
            val m = Method(tempData?.targetMethodName, tempData?.targetMethodDesc)
            val mg = GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cv)


            when (tempData?.hookName) {
                WEBVIEW_SYSTEM -> createWebViewInsnList(mg)
                WEBVIEW_X5 -> createX5WebViewInsnList(mg)
                WEBVIEW_UC -> createUCWebViewInsnList(mg)
            }
            mg.loadThis()
            mg.loadArgs()
            mg.invokeConstructor(
                Type.getObjectType(tempData?.targetClassName?.unNormalize()),
                Method(tempData?.targetMethodName, tempData?.targetMethodDesc)
            )
            mg.returnValue()
            mg.endMethod()
        }
        super.visitEnd()
    }

    override fun getTargetHookData(): List<HookInjectorClass.HookData> {
        return HookInjectorClass.WEBVIEW_CLASSES
    }


    override fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            WEBVIEW_SYSTEM -> createWebViewInsnList(adviceAdapter)
            WEBVIEW_X5 -> createX5WebViewInsnList(adviceAdapter)
            WEBVIEW_UC -> createUCWebViewInsnList(adviceAdapter)
        }
    }

    private fun createWebViewInsnList(adviceAdapter: GeneratorAdapter) {
        "add CircleJs to WebView succeed:${context.className}".println()
        adviceAdapter.apply {
            //调用 GioWebView addCircleJsToWebView
            visitVarInsn(Opcodes.ALOAD, 1)
            visitVarInsn(Opcodes.ILOAD, 2)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioWebView",
                "addCircleJsToWebView",
                "(Landroid/webkit/WebView;I)V",
                false
            )
            generateHookData = null
        }
    }

    private fun createX5WebViewInsnList(adviceAdapter: GeneratorAdapter) {
        "add CircleJs to X5WebView succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 1)
            visitVarInsn(Opcodes.ILOAD, 2)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioWebView",
                "addCircleJsToX5",
                "(Lcom/tencent/smtt/sdk/WebView;I)V",
                false
            )
            generateHookData = null
        }
    }

    private fun createUCWebViewInsnList(adviceAdapter: GeneratorAdapter) {
        "add CircleJs to UCWebView succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 1)
            visitVarInsn(Opcodes.ILOAD, 2)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioWebView",
                "addCircleJsToUc",
                "(Lcom/uc/webview/export/WebView;I)V",
                false
            )
            generateHookData = null
        }
    }

}