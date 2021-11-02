package com.growingio.giokit.plugin.transform.v3

import com.didiglobal.booster.kotlinx.asIterable
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

        // okhttp
        if (className == "com.growingio.android.okhttp3.OkHttpDataLoader\$Factory") {
            klass.methods?.find {
                it.name == "getsInternalClient" && it.desc != "()V"
            }.let {
                "hook OkHttpDataLoader succeed: ${className}_${it?.name}_${it?.desc}".println()
                it?.instructions
                    ?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)
                    ?.filter { method ->
                        method.opcode == Opcodes.INVOKEVIRTUAL
                                && method.owner == "okhttp3/OkHttpClient\$Builder"
                                && method.name == "addInterceptor"
                                && method.desc == "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient\$Builder;"
                    }?.forEach { fieldInsnNode ->
                        it.instructions.insert(fieldInsnNode, insertOkHttpClientInsnList())
                    }
            }
        }

        // url connection
        if (className == "com.growingio.android.urlconnection.UrlConnectionFetcher") {
            klass.methods?.find {
                it.name == "loadDataWithRedirects"
            }.let {
                "hook urlconnection succeed:${className}_${it?.name}_${it?.desc}".println()
                it?.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter { method ->
                        method.opcode == Opcodes.INVOKESTATIC
                                && method.owner == "com/growingio/android/urlconnection/UrlConnectionFetcher"
                                && method.name == "getHttpStatusCodeOrInvalid"
                                && method.desc == "(Ljava/net/HttpURLConnection;)I"
                    }?.forEach { fieldInsnNode ->
                        // because method "getHttpStatusCodeOrInvalid" return a value to "istore 6", so insert to "fieldInsnNode.next"
                        it.instructions.insert(fieldInsnNode.next, insertUrlConnectionInsnList())
                    }
            }
        }

        // volley
        if (className == "com.growingio.android.volley.VolleyDataFetcher\$GioRequest") {
            klass.methods?.find { it.name == "parseNetworkResponse" }.let {
                "hook volley response succeed:${className}_${it?.name}_${it?.desc}".println()
                it?.instructions?.insert(insertVolleySuccessInsnList())
            }

            klass.methods?.find { it.name == "parseNetworkError" }.let {
                "hook volley error succeed:${className}_${it?.name}_${it?.desc}".println()
                it?.instructions?.insert(insertVolleyErrorInsnList())
            }
        }

        return klass
    }

    private fun insertVolleyErrorInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioHttp",
                    "parseGioKitVolleyError",
                    "(Lcom/android/volley/Request;Lcom/android/volley/VolleyError;)V",
                    false
                )
            )
            this
        }
    }

    private fun insertVolleySuccessInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioHttp",
                    "parseGioKitVolleySuccess",
                    "(Lcom/android/volley/Request;Lcom/android/volley/NetworkResponse;)V",
                    false
                )
            )

            this
        }
    }


    //GioHttp.parseGioKitUrlConnection(urlConnection: HttpURLConnection, headers: Map<String, String>, data: ByteArray)
    private fun insertUrlConnectionInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                FieldInsnNode(
                    Opcodes.GETFIELD,
                    "com/growingio/android/urlconnection/UrlConnectionFetcher",
                    "urlConnection",
                    "Ljava/net/HttpURLConnection;"
                )
            )
            add(VarInsnNode(Opcodes.ALOAD, 4))
            add(VarInsnNode(Opcodes.ALOAD, 5))
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


    private fun insertOkHttpClientInsnList(): InsnList {
        return with(InsnList()) {
            //插入 GioHttpCaptureIntercept 拦截器
            add(TypeInsnNode(Opcodes.NEW, "com/growingio/giokit/hook/GioHttpCaptureInterceptor"))
            add(InsnNode(Opcodes.DUP))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/growingio/giokit/hook/GioHttpCaptureInterceptor",
                    "<init>",
                    "()V",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "okhttp3/OkHttpClient\$Builder",
                    "addInterceptor",
                    "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient\$Builder;",
                    false
                )
            )
            this
        }

    }
}