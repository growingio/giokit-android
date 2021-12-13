package com.growingio.giokit.plugin.transform.saas

import com.growingio.giokit.plugin.base.asIterable
import com.growingio.giokit.plugin.transform.ClassTransformer
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.className
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *     抓取所有的网络请求并展示
 * @author cpacm 2021/10/27
 */
class GioHttpTransformer : ClassTransformer {

    override fun transform(context: GioTransformContext, klass: ClassNode): ClassNode {
        val className = klass.className


        // url connection
        if (className == "com.growingio.android.sdk.data.net.HttpService") {
            klass.methods?.find {
                it.name == "performRequest" && Opcodes.ACC_PRIVATE == it.access
            }.let {
                "hook urlconnection succeed:${className}_${it?.name}_${it?.desc}".println()
                // find methodVisitor.visitFieldInsn(GETSTATIC, "com/growingio/android/sdk/collection/GConfig", "DEBUG", "Z");
                it?.instructions?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(FieldInsnNode::class.java)
                    ?.filter { method ->
                        method.opcode == Opcodes.GETSTATIC
                                && method.owner == "com/growingio/android/sdk/collection/GConfig"
                                && method.name == "DEBUG"
                                && method.desc == "Z"
                    }?.forEach { fieldInsnNode ->
                        // 插入 `if (GConfig.DEBUG)` 位置前
                        it.instructions.insertBefore(
                            fieldInsnNode,
                            insertUrlConnectionInsnList()
                        )
                    }
            }
        }


        return klass
    }


    //GioHttp.parseGioKitUrlConnection(urlConnection: HttpURLConnection, headers: Map<String, String>, data: ByteArray)
    //            methodVisitor.visitVarInsn(ALOAD, 5);
    //            methodVisitor.visitVarInsn(ALOAD, 0);
    //            methodVisitor.visitFieldInsn(GETFIELD, "com/growingio/android/sdk/data/net/HttpService", "mHeaders", "Ljava/util/Map;");
    //            methodVisitor.visitVarInsn(ALOAD, 0);
    //            methodVisitor.visitFieldInsn(GETFIELD, "com/growingio/android/sdk/data/net/HttpService", "mData", "[B");
    //            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/growingio/android/sdk/data/net/GioHttp", "parseGioKitUrlConnection", "(Ljava/net/HttpURLConnection;Ljava/util/Map;[B)V", false);
    //
    private fun insertUrlConnectionInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 5))
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                FieldInsnNode(
                    Opcodes.GETFIELD,
                    "com/growingio/android/sdk/data/net/HttpService",
                    "mHeaders",
                    "Ljava/util/Map;"
                )
            )
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                FieldInsnNode(
                    Opcodes.GETFIELD,
                    "com/growingio/android/sdk/data/net/HttpService",
                    "mData",
                    "[B"
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioHttp",
                    "parseGioKitUrlConnection",
                    "(Ljava/net/HttpURLConnection;Ljava/util/Map;[B)V",
                    false
                )
            )
            this
        }
    }
}