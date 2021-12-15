package com.growingio.giokit.plugin.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.growingio.giokit.plugin.base.*
import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.println
import java.io.File
import java.net.URI
import java.util.concurrent.*

/**
 * Represents a delegate of TransformInvocation
 *
 */
internal class GioKitTransformInvocation(
    private val delegate: TransformInvocation,
    internal val transform: GioKitBaseTransform,
) : TransformInvocation by delegate, GioTransformContext {

    private val project = transform.project

    private val outputs = CopyOnWriteArrayList<File>()

    override val gioConfig = transform.gioConfig

    override val name: String = delegate.context.variantName

    override val projectDir: File = project.projectDir

    override val buildDir: File = project.buildDir

    override val temporaryDir: File = delegate.context.temporaryDir

    override val reportsDir: File = File(buildDir, "reports").also { it.mkdirs() }

    override val bootClasspath = delegate.getBootClasspath(project)

    override val compileClasspath = delegate.compileClasspath

    override val runtimeClasspath = delegate.getRuntimeClasspath(project)

    override val klassPool: AbstractKlassPool =
        object : AbstractKlassPool(compileClasspath, transform.bootKlassPool) {}

    override val applicationId = getVariant(project).applicationId

    override val isDebuggable = getVariant(project).buildType.isDebuggable

    override val isDataBindingEnabled = isDataBindingEnabled(project)

    override fun hasProperty(name: String) = project.hasProperty(name)

    override fun <T> getProperty(name: String, default: T): T = project.getProperty(name, default)

    private fun onPreTransform() {
        transform.transformers.forEach {
            it.onPreTransform(this)
        }
    }

    private fun onPostTransform() {
        transform.transformers.forEach {
            it.onPostTransform(this)
        }
    }

    private fun doTransform(block: (ExecutorService) -> Iterable<Future<*>>) {
        this.outputs.clear()
        this.onPreTransform()

        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            block(executor).forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }

        this.onPostTransform()
    }

    private val latestFileMap: HashMap<File, File> = hashMapOf()

    internal fun doLatestTransform() {
        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            latestFileMap.map {
                executor.submit {
                    it.key.transform(it.value) { bytecode ->
                        bytecode.transformLatest()
                    }
                }
            }.forEach { it.get() }
        } finally {
            latestFileMap.clear()
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }
    }

    internal fun doFullTransform() {
        this.outputs.clear()
        this.onPreTransform()

        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            this.inputs.map {
                it.jarInputs + it.directoryInputs
            }.flatten().map { input ->
                executor.submit {
                    val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
                    outputProvider?.let { provider ->
                        project.logger.info("Transforming ${input.file}")
                        input.transform(
                            provider.getContentLocation(
                                input.name,
                                input.contentTypes,
                                input.scopes,
                                format
                            )
                        )
                    }
                }
            }.forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }
        this.onPostTransform()
    }

    internal fun doIncrementalTransform() {
        this.outputs.clear()
        this.onPreTransform()

        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            this.inputs.map { input ->
                input.jarInputs.filter { it.status != Status.NOTCHANGED }.map { jarInput ->
                    executor.submit {
                        doIncrementalTransform(jarInput)
                    }
                } + input.directoryInputs.filter { it.changedFiles.isNotEmpty() }.map { dirInput ->
                    val base = dirInput.file.toURI()
                    executor.submit {
                        doIncrementalTransform(dirInput, base)
                    }
                }
            }.flatten().forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }

        this.onPostTransform()
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(jarInput: JarInput) {
        when (jarInput.status) {
            Status.REMOVED -> jarInput.file.delete()
            Status.CHANGED, Status.ADDED -> {
                project.logger.info("Transforming ${jarInput.file}")
                outputProvider?.let { provider ->
                    jarInput.transform(
                        provider.getContentLocation(
                            jarInput.name,
                            jarInput.contentTypes,
                            jarInput.scopes,
                            Format.JAR
                        )
                    )
                }
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(dirInput: DirectoryInput, base: URI) {
        dirInput.changedFiles.forEach { (file, status) ->
            when (status) {
                Status.REMOVED -> {
                    project.logger.info("Deleting $file")
                    outputProvider?.let { provider ->
                        provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        ).parentFile.listFiles()?.asSequence()
                            ?.filter { it.isDirectory }
                            ?.map { File(it, dirInput.file.toURI().relativize(file.toURI()).path) }
                            ?.filter { it.exists() }
                            ?.forEach { it.delete() }
                    }
                    file.delete()
                }
                Status.ADDED, Status.CHANGED -> {
                    project.logger.info("Transforming $file")
                    outputProvider?.let { provider ->
                        val root = provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        )
                        val output = File(root, base.relativize(file.toURI()).path)
                        outputs += output
                        file.transform(output) { bytecode ->
                            bytecode.transform(file, output)
                        }
                    }
                }
            }
        }
    }

    fun QualifiedContent.transform(output: File) {
        outputs += output
        try {
            this.file.transform(output) { bytecode ->
                bytecode.transform(this.file, output)
            }
        } catch (e: Exception) {
            "e===>${e.message}".println()
            e.printStackTrace()
        }

    }

    private fun ByteArray.transform(file: File, output: File): ByteArray {
        return transform.transformers.fold(this) { bytes, transformer ->
            transformer.transform(this@GioKitTransformInvocation, bytes) { hasLatest ->
                if (hasLatest) {
                    latestFileMap.put(file, output)
                }
            }
        }
    }

    private fun ByteArray.transformLatest(): ByteArray {
        return transform.transformers.fold(this) { bytes, transformer ->
            transformer.transformLatest(this@GioKitTransformInvocation, bytes)
        }
    }
}