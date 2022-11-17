package com.growingio.giokit.utils

import android.content.Context
import android.os.Environment

import java.io.File

import java.io.RandomAccessFile
import java.nio.charset.Charset

const val ERROR_CACHE_DIR = "error"

fun Context.getCacheFile(): File {
    var fileDir: File? = null
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
        fileDir = externalCacheDir
    }
    if (fileDir == null) {
        fileDir = cacheDir
    }

    return fileDir!!
}

fun Context.generateErrorFile(name: String, content: String): String {
    val dir = File(getCacheFile().path + File.separator + ERROR_CACHE_DIR)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val errorFile = File(dir, name)
    if (!errorFile.exists()) {
        errorFile.createNewFile()
    }
    val raf = RandomAccessFile(errorFile, "rw")
    raf.writeBytes(content)
    raf.close()

    return errorFile.path
}

fun Context.loadError(path: String): String {
    val errorLog = File(path)
    if (!errorLog.exists()) return ""
    val raf = RandomAccessFile(errorLog, "r")
//    if (raf.length() <= Int.MAX_VALUE) {
//        val bytes = ByteArray(raf.length().toInt())
//        raf.readFully(bytes)
//        raf.close()
//        return bytes.toString(Charset.defaultCharset())
//    } else {
//        val sb = StringBuilder()
//        while (raf.readLine() != null) {
//            sb.append(raf)
//        }
//        raf.close()
//        return sb.toString()
//    }
    val sb = StringBuilder()
    var line = raf.readLine()
    while (line != null) {
        sb.append(line).appendLine()
        line = raf.readLine()
    }
    raf.close()
    return sb.toString()
}

fun Context.cleanOutDatedFile(time: Long) {
    val dir = File(getCacheFile().path + File.separator + ERROR_CACHE_DIR)
    if (!dir.exists() || dir.listFiles() == null) return

    for (file in dir.listFiles()!!) {
        if (file.lastModified() < time) {
            file.delete()
        }
    }
}