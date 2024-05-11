package com.munir_atef.pha_viewer.service_groups.filesystem

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.munir_atef.pha_viewer.service_groups.ServiceInterface
import com.munir_atef.pha_viewer.service.ServiceResult
import java.io.File


object FilesystemInterface: ServiceInterface {
    // "::files/"
    private val filesystem = Filesystem()
    private val gson = Gson()

    override fun invoke(service: String, body: String): ServiceResult {
        val result: ServiceResult = when (service) {
            "info" -> info(body)
            "delete" -> delete(body)
            "create-file" -> createFile(body)
            "create-dir" -> createDir(body)
            "list-content" -> listDirContent(body)
            "copy" -> copyFile(body)
            "move" -> moveFile(body)
            "rename" -> rename(body)
            "read-file" -> readAsBytes(body)
            "write-string" -> writeString(body)
            "write-bytes" -> writeBytes(body)

            else -> ServiceResult(null, false)
        }

        return result
    }


    /** Expected body: filePath */
    private fun info(body: String): ServiceResult {
        val filePath = FilePath(body)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val fileInfo: JsonObject = filesystem.info(path)
        return ServiceResult(fileInfo, true)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun delete(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)
        val rawPath: String? = args.get("path")?.asString
        val recursively: Boolean = args.get("recursively")?.asBoolean ?: false

        val filePath = FilePath(rawPath)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.delete(path, recursively)
        return ServiceResult(null, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun createFile(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)
        val rawPath: String? = args.get("path")?.asString
        val recursively: Boolean = args.get("recursively")?.asBoolean ?: false

        val filePath = FilePath(rawPath)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.createFile(path, recursively)
        return ServiceResult(null, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun createDir(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)
        val rawPath: String? = args.get("path")?.asString
        val recursively: Boolean = args.get("recursively")?.asBoolean ?: false

        val filePath = FilePath(rawPath)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.createDir(path, recursively)
        return ServiceResult(null, result)
    }

    /** Expected body: filePath */
    private fun listDirContent(body: String): ServiceResult {
        val filePath = FilePath(body)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val listedContent: JsonArray = filesystem.listDirContent(path)
            ?: return ServiceResult(null, false)
        return ServiceResult(listedContent, true)
    }

    /**
     * Expected body: {
     *     "src": String,
     *     "dest": String,
     *     "overwrite": Boolean?,
     *     "recursively": Boolean?
     * }
     * */
    private fun copyFile(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)

        val src: String? = args.get("src")?.asString
        val dest: String? = args.get("dest")?.asString
        val recursively: Boolean = args.get("recursively")?.asBoolean ?: false
        val overwrite: Boolean = args.get("overwrite")?.asBoolean ?: true

        val srcFilePath = FilePath(src)
        val srcPath = srcFilePath.getPath() ?: return ServiceResult(null, false)
        val destFilePath = FilePath(dest)
        val destPath = destFilePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.copyFile(srcPath, destPath, overwrite, recursively)
        return ServiceResult(null, result)
    }

    /**
     * Expected body: {
     *     "src": String,
     *     "dest": String,
     *     "overwrite": Boolean?,
     *     "recursively": Boolean?
     * }
     * */
    private fun moveFile(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)

        val src: String? = args.get("src")?.asString
        val dest: String? = args.get("dest")?.asString
        val overwrite: Boolean = args.get("overwrite")?.asBoolean ?: true

        val srcFilePath = FilePath(src)
        val srcPath = srcFilePath.getPath() ?: return ServiceResult(null, false)
        val destFilePath = FilePath(dest)
        val destPath = destFilePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.moveFile(srcPath, destPath, overwrite)
        return ServiceResult(null, result)
    }

    /**
     * Expected body: {
     *     "src": String,
     *     "dest": String,
     *     "overwrite": Boolean?,
     *     "recursively": Boolean?
     * }
     * */
    private fun rename(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)

        val src: String? = args.get("src")?.asString
        val dest: String? = args.get("dest")?.asString
        val overwrite: Boolean = args.get("overwrite")?.asBoolean ?: true

        val srcFilePath = FilePath(src)
        val srcPath = srcFilePath.getPath() ?: return ServiceResult(null, false)
        val destFilePath = FilePath(dest)
        val destPath = destFilePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.renameFile(srcPath, destPath, overwrite)
        return ServiceResult(null, result)
    }

    /** Expected body: filePath */
    private fun readAsBytes(body: String): ServiceResult {
        val filePath = FilePath(body)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        return ServiceResult(File(path), true)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "content": String,
     *     "overwrite": Boolean?
     * }
     * */
    private fun writeString(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)

        val rawPath: String? = args.get("path")?.asString
        val content: String = args.get("content")?.asString ?: return ServiceResult(null, false)
        val overwrite: Boolean = args.get("overwrite")?.asBoolean ?: true

        val filePath = FilePath(rawPath)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val result: Boolean = filesystem.writeString(path, content, overwrite)
        return ServiceResult(null, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "content": JSONArray<Int>,
     *     "overwrite": Boolean?
     * }
     * */
    private fun writeBytes(body: String): ServiceResult {
        val args = gson.fromJson(body, JsonObject::class.java)

        val rawPath: String? = args.get("path")?.asString
        val content: JsonArray = args.get("content")?.asJsonArray ?: return ServiceResult(null, false)
        val overwrite: Boolean = args.get("overwrite")?.asBoolean ?: true

        val filePath = FilePath(rawPath)
        val path = filePath.getPath() ?: return ServiceResult(null, false)

        val contentLength: Int = content.size()
        val contentBytes = ByteArray(contentLength)
        for (i: Int in 0 until contentLength)
            contentBytes[i] = content.get(i).asInt.toByte()

        val result: Boolean = filesystem.writeBytes(path, contentBytes, overwrite)
        return ServiceResult(null, result)
    }
}



