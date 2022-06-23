package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.HTTP_V3_OKHTTP
import com.growingio.giokit.plugin.transform.HookInjectorClass.HTTP_V3_URLCONN
import com.growingio.giokit.plugin.transform.HookInjectorClass.HTTP_V3_VOLLEY_FAIL
import com.growingio.giokit.plugin.transform.HookInjectorClass.HTTP_V3_VOLLEY_SUCCESS
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
class GioHttpVisitor(api: Int, ncv: ClassVisitor, context: GioTransformContext) :
    GioBaseClassVisitor(api, ncv, context) {


    override fun getTargetHookData(): List<HookInjectorClass.HookData> {
        return HookInjectorClass.HTTP_V3_CLASSES
    }

    override fun generateAroundInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            HTTP_V3_OKHTTP -> insertOkHttpClientInsnList(adviceAdapter)
            HTTP_V3_URLCONN -> insertUrlConnectionInsnList(adviceAdapter)
        }
    }

    override fun generateMethodInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            HTTP_V3_VOLLEY_SUCCESS -> insertVolleySuccessInsnList(adviceAdapter)
            HTTP_V3_VOLLEY_FAIL -> insertVolleyErrorInsnList(adviceAdapter)
        }
    }


    private fun insertVolleySuccessInsnList(adviceAdapter: AdviceAdapter) {
        "hook volley succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioHttp",
                "parseGioKitVolleySuccess",
                "(Lcom/android/volley/Request;Lcom/android/volley/NetworkResponse;)V",
                false
            )
        }
    }

    private fun insertVolleyErrorInsnList(adviceAdapter: AdviceAdapter) {
        "hook volley error succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioHttp",
                "parseGioKitVolleyError",
                "(Lcom/android/volley/Request;Lcom/android/volley/VolleyError;)V",
                false
            )
        }
    }


    private fun insertOkHttpClientInsnList(adviceAdapter: AdviceAdapter) {
        "hook OkHttpDataLoader succeed: ${context.className}".println()
        adviceAdapter.apply {
            visitTypeInsn(Opcodes.NEW, "com/growingio/giokit/hook/GioHttpCaptureInterceptor")
            visitInsn(Opcodes.DUP)
            visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/growingio/giokit/hook/GioHttpCaptureInterceptor",
                "<init>",
                "()V",
                false
            )
            visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "okhttp3/OkHttpClient\$Builder",
                "addInterceptor",
                "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient\$Builder;",
                false
            )
        }
    }

    private fun insertUrlConnectionInsnList(adviceAdapter: AdviceAdapter) {
        "hook urlconnection succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 0)
            visitFieldInsn(
                Opcodes.GETFIELD,
                "com/growingio/android/urlconnection/UrlConnectionFetcher",
                "urlConnection",
                "Ljava/net/HttpURLConnection;"
            )

            visitVarInsn(Opcodes.ALOAD, 4)
            visitVarInsn(Opcodes.ALOAD, 5)

            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/growingio/giokit/hook/GioHttp",
                "parseGioKitUrlConnection",
                "(Ljava/net/HttpURLConnection;Ljava/util/Map;[B)V",
                false
            )
        }
    }

}