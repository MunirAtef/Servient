package com.munir_atef.pha_viewer.service_groups.filesystem

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class Filesystem {
    fun info(filePath: String): JsonObject {
        val file = File(filePath)
        val isExists = file.exists()
        val fileInfo = JsonObject()
        fileInfo.addProperty("isExists", isExists)

        if (!isExists) return fileInfo

        val type: String = if (file.isFile) "f" else if (file.isDirectory) "d" else "o"

        fileInfo.addProperty("parentPath", file.parent)
        fileInfo.addProperty("name", file.name)
        fileInfo.addProperty("type", type)
        fileInfo.addProperty("size", file.length())
        fileInfo.addProperty("lastModified", file.lastModified())

        return fileInfo
    }

    fun delete(filePath: String, recursively: Boolean): Boolean {
        val file = File(filePath)
        val isExists = file.exists()
        if (isExists) {
            if (recursively) file.deleteRecursively()
            else file.delete()
        }
        return isExists
    }

    fun createFile(filePath: String, recursively: Boolean): Boolean {
        val file = File(filePath)

        return if (recursively) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        } else file.createNewFile()
    }

    fun createDir(dirPath: String, recursively: Boolean): Boolean {
        val file = File(dirPath)
        if (recursively) return file.mkdirs()
        return file.mkdir()
    }

    fun listDirContent(dirPath: String): JsonArray? {
        val rootFile = File(dirPath)
        if (!rootFile.isDirectory) return null

        val listedFiles: Array<File> = rootFile.listFiles() ?: return null
        val filesJson = JsonArray()

        listedFiles.forEach {
            filesJson.add(info(it.path))
        }

        return filesJson
    }

    fun copyFile(srcPath: String, destPath: String, overwrite: Boolean, recursively: Boolean): Boolean {
        val srcFile = File(srcPath)
        if (!srcFile.exists()) return false

        if (recursively) srcFile.copyRecursively(File(destPath), overwrite = overwrite)
        else srcFile.copyTo(File(destPath), overwrite)

        return true
    }

    fun moveFile(srcPath: String, destPath: String, overwrite: Boolean): Boolean {
        val srcFile = File(srcPath)
        if (!srcFile.exists()) return false


        val sourcePath = Paths.get(srcPath)
        val targetPath = Paths.get(destPath)


        val option: StandardCopyOption? = if (overwrite) StandardCopyOption.REPLACE_EXISTING else null
        Files.move(sourcePath, targetPath, option)
        return true
    }

    fun renameFile(srcPath: String, destPath: String, overwrite: Boolean): Boolean {
        val srcFile = File(srcPath)
        val destFile = File(destPath)
        if (!srcFile.exists()) return false

        if (destFile.exists()) {
            if (overwrite) destFile.delete()
            else return false
        }

        srcFile.renameTo(destFile)
        return true
    }


    fun writeString(filePath: String, content: String, overwrite: Boolean): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        if (overwrite) file.writeText(content)
        else file.appendText(content)
        return true
    }

    fun writeBytes(filePath: String, content: ByteArray, overwrite: Boolean): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        if (overwrite) file.writeBytes(content)
        else file.appendBytes(content)
        return true
    }
}

