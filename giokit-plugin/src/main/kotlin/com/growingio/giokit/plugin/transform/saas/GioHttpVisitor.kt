package com.growingio.giokit.plugin.transform.saas

import com.growingio.giokit.plugin.transform.GioBaseClassVisitor
import com.growingio.giokit.plugin.transform.HookInjectorClass
import com.growingio.giokit.plugin.transform.HookInjectorClass.HTTP_SAAS_URLCONN
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
        return HookInjectorClass.HTTP_SAAS_CLASSES
    }

    override fun generateAroundInsn(name: String, adviceAdapter: AdviceAdapter) {
        when (name) {
            HTTP_SAAS_URLCONN -> insertUrlConnectionInsnList(adviceAdapter)
        }
    }

    private fun insertUrlConnectionInsnList(adviceAdapter: AdviceAdapter) {
        "hook saas UrlConnection succeed:${context.className}".println()
        adviceAdapter.apply {
            visitVarInsn(Opcodes.ALOAD, 5)
            visitVarInsn(Opcodes.ALOAD, 0)
            visitFieldInsn(
                Opcodes.GETFIELD,
                "com/growingio/android/sdk/data/net/HttpService",
                "mHeaders",
                "Ljava/util/Map;"
            )

            visitVarInsn(Opcodes.ALOAD, 0)
            visitFieldInsn(
                Opcodes.GETFIELD,
                "com/growingio/android/sdk/data/net/HttpService",
                "mData",
                "[B"
            )

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