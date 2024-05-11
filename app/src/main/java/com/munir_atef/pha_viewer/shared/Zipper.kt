package com.munir_atef.pha_viewer.shared

import java.io.*
import java.nio.file.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.pathString


object Zipper {
    fun unzip(zipFilePath: String, destDirectory: String) {
        File(destDirectory).run { if (!exists()) { mkdirs() } }

        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val filePath = destDirectory + File.separator + entry.name

                    if (!entry.isDirectory) extractFile(input, filePath)
                    else File(filePath).mkdirs()
                }

            }
        }
    }

    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        File(Paths.get(destFilePath).parent.pathString).run { if (!exists()) { mkdirs() } }

        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }



    fun zipAll(directory: String, zipFilePath: String) {
        val sourceFile = File(directory)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFilePath))).use {
            zipFiles(it, sourceFile, "")
        }
    }

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
        val data = ByteArray(BUFFER_SIZE)

        for (file in sourceFile.listFiles() ?: emptyArray()) {
            val path = parentDirPath + file.name

            if (file.isDirectory) {
                val entry = ZipEntry(path + File.separator)
                entry.time = file.lastModified()
                entry.isDirectory
                entry.size = file.length()

                zipOut.putNextEntry(entry)

                //Call recursively to add files within this directory
                zipFiles(zipOut, file, path + File.separator)
            } else {
                if (!file.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                    FileInputStream(file).use { fi ->
                        BufferedInputStream(fi).use { origin ->
                            val entry = ZipEntry(path)
                            entry.time = file.lastModified()
                            entry.isDirectory
                            entry.size = file.length()
                            zipOut.putNextEntry(entry)
                            while (true) {
                                val readBytes = origin.read(data)
                                if (readBytes == -1) {
                                    break
                                }
                                zipOut.write(data, 0, readBytes)
                            }
                        }
                    }
                } else {
                    zipOut.closeEntry()
                    zipOut.close()
                }
            }
        }
    }


    fun getFileFromZip(zipFile: File, fileName: String): ByteArray? {
        ZipFile(zipFile).use { zip ->
            zip.getEntry(fileName)?.let { entry ->
                zip.getInputStream(entry).use { input ->
                    return input.readBytes()
                }
            }
        }
        return null
    }


    @Deprecated("it will be removed soon")
    fun fileExistsOnZip2(zipPath: String): Boolean {
        try {
            val sourceZipFile = ZipFile(zipPath)

            //get all entries
            val entries: Enumeration<*> = sourceZipFile.entries()
            var manifestFound = false
            var srcFound = false

            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                println("Entry: " + entry.name)
                if (entry.name == "/manifest.json") manifestFound = true
                else if (entry.name == "/src/") srcFound = true
                if (srcFound && manifestFound) break
            }

            println("src: $srcFound")
            println("manifest: $manifestFound")

            sourceZipFile.close()
            return manifestFound && srcFound
        } catch (ioe: IOException) {
            println("Error opening zip file: $ioe")
            return false
        }
    }


    fun checkIsValid(zipPath: String): Boolean {
        try {
            ZipFile(zipPath).use { zip ->
                val manifest: ZipEntry? = zip.getEntry("manifest.json")
                val src: ZipEntry? = zip.getEntry("src/")

                return manifest != null && !manifest.isDirectory && src != null && src.isDirectory
            }
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }

    private const val BUFFER_SIZE = 4096
}


