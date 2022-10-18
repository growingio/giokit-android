package com.growingio.giokit.plugin.transform.saas

import com.growingio.android.plugin.utils.info
import com.growingio.android.plugin.utils.normalize
import com.growingio.android.plugin.utils.unNormalize
import com.growingio.giokit.plugin.utils.GioTrackHook
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.println
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import java.io.*
import javax.lang.model.element.Modifier


/**
 * <p>
 *
 * @author cpacm 2022/6/16
 */
class GioCodeVisitor(api: Int, ncv: ClassVisitor, val context: GioTransformContext) :
    ClassVisitor(api, ncv) {

    private var shouldFindCode = false
    private val findedMethod: HashSet<GioTrackHook> = hashSetOf()
    private val findDomain: HashSet<String> = hashSetOf()
    val trackHooks = hashSetOf(
        GioTrackHook("com.growingio.android.sdk.collection.GrowingIO", "track"),
        GioTrackHook("com.growingio.android.sdk.collection.GrowingIO", "trackPage"),
    )

    init {
        if (context.gioConfig.trackFinder.enable) {
            if (context.gioConfig.trackFinder.domain.isNotEmpty()) {
                findDomain.addAll(context.gioConfig.trackFinder.domain)
            }
            findDomain.add(context.gioConfig.domain)
            if (context.gioConfig.trackFinder.className.isNotEmpty() && context.gioConfig.trackFinder.methodName.isNotEmpty()) {
                trackHooks.add(
                    GioTrackHook(
                        context.gioConfig.trackFinder.className, context.gioConfig.trackFinder.methodName
                    )
                )
            }
        }
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        findDomain.forEach {
            if (name != null && normalize(name).startsWith(it)) {
                shouldFindCode = true
            }
        }
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (!shouldFindCode) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }

        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodFindVisitor(api, mv, access, name, descriptor)
    }

    override fun visitEnd() {
        if (findedMethod.isEmpty()) return super.visitEnd()

        findedMethod.forEach {
            it.toString().println()
        }

        generateCode(hookGenerateCode())

        super.visitEnd()
    }

    fun generateCode(codeSet: Set<String>) {
        context.generatedDir.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        "creating a class with public modifier and writing it to genDir".println()
        context.generatedDir.path.println()

        // creating a class DlkanthDemo with public modifier and writing it to genDir
        val classBuilder = TypeSpec.classBuilder("GioCode").superclass(
            ParameterizedTypeName.get(
                HashSet::class.java,
                String::class.java
            )
        )
        classBuilder.addModifiers(Modifier.PUBLIC)
        val builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        codeSet.forEach {
            val code = "add(\"${it.replace("$","#")}\")"
            builder.addStatement(code)
        }

        classBuilder.addMethod(
            builder.build()
        )

        val javaFile = JavaFile.builder("com.growingio.giokit", classBuilder.build()).build()
        javaFile.writeTo(context.generatedDir)
    }

    // 在增量编译的情况下，无法解决->文件删除时立马更新的问题
    private fun hookGenerateCode(): HashSet<String> {
        val codeSet = hashSetOf<String>()
        try {
            val file = context.gioConfig.getVisitorCodeFile()
            if (!file.exists()) {
                file.createNewFile()
            } else {
                val fr = FileReader(file)
                val br = BufferedReader(fr)
                br.lineSequence().forEach {
                    codeSet.add(it)
                }
            }
            codeSet.removeIf {
                it.startsWith(context.className + "::")
            }
            findedMethod.forEach {
                codeSet.add(it.className + "::" + it.methodName)
            }
            val fw = FileWriter(file, false)
            val bw = BufferedWriter(fw)
            codeSet.forEach {
                bw.write(it)
                bw.newLine()
            }
            bw.flush()
            bw.close()
            fw.close()
        } catch (e: IOException) {
            info(e.message ?: "can't generate code file")
        }
        return codeSet
    }

    inner class MethodFindVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
    ) : AdviceAdapter(api, nmv, access, name, descriptor) {

        var index = 1
        override fun visitMethodInsn(
            opcodeAndSource: Int,
            owner2: String?,
            name2: String?,
            descriptor: String?,
            isInterface: Boolean
        ) {
            val gName = name.let {
                if (index > 1) "$it-$index"
                else it
            }
            trackHooks.filter { owner2 == it.className.unNormalize() && name2 == it.methodName }.forEach {
                findedMethod.add(GioTrackHook(context.className, gName, owner2!!, name2!!))
                "[generate index]:$index:${context.className} - ${name}".println()
                index += 1
            }

            super.visitMethodInsn(opcodeAndSource, owner2, name2, descriptor, isInterface)
        }
    }
}