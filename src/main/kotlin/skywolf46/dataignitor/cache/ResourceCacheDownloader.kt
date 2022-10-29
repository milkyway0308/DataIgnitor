package skywolf46.dataignitor.cache

import skywolf46.dataignitor.data.SharedResourceIndex
import skywolf46.dataignitor.util.DataSizeUtil
import skywolf46.dataignitor.util.printError
import java.io.File
import java.net.URL
import java.net.URLConnection
import java.util.zip.DeflaterInputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import kotlin.system.exitProcess

object ResourceCacheDownloader {
    fun downloadIfNotExists(targetServer: String, res: SharedResourceIndex, path: File, bufferSize: Long): Boolean {
        val targetFile = res.toSubDirectory(path)
        if (targetFile.exists())
            return false
        download(targetServer, res, path, bufferSize)
        return true
    }

    fun download(targetServer: String, res: SharedResourceIndex, path: File, bufferSize: Long) {
        val timeStarted = System.currentTimeMillis()
        val url = URL("$targetServer${res.resourcePath}")
        val fileTemp = res.toSubDirectory(path, ".tmp")
        val buffer = ByteArray(bufferSize.toInt())
        try {
            println("Download started for file \"${res.fullPath}\"")
            fileTemp.parentFile.mkdirs()
            fileTemp.createNewFile()
            url.openStream().use { serverStream ->
                fileTemp.deleteOnExit()
                fileTemp.outputStream().use { opt ->
                    do {
                        val read = serverStream.read(buffer)
                        if (read == -1)
                            break
                        opt.write(buffer, 0, read)
                    } while (true)
                }
            }
            println("Validating \"${res.fullPath}\"..")
            if (fileTemp.length() != res.compressedFileSize) {
                printError(
                    "Failed to download \"${res.fullPath}\" : Compressed File size incorrect (Downloaded ${
                        DataSizeUtil.compact(
                            fileTemp.length()
                        )
                    } / Origin ${DataSizeUtil.compact(res.compressedFileSize)})"
                )
                exitProcess(-1)
            }
            println("Unzipping \"${res.fullPath}\"..")
            val unzipStart = System.currentTimeMillis()
            unzip(fileTemp, res.toSubDirectory(path), buffer, res)
            val elapsed = System.currentTimeMillis() - timeStarted
            val unzipElapsed = System.currentTimeMillis() - unzipStart
            println("Downloaded file \"${res.fullPath}\" in ${elapsed - unzipElapsed}ms (Unzip ${unzipElapsed}ms)")
        } catch (e: Throwable) {
            printError("Failed to download \"${res.fullPath}\" : ${e.javaClass.name}")
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    private fun unzip(from: File, to: File, buffer: ByteArray, res: SharedResourceIndex) {
        to.parentFile.mkdirs()
        to.createNewFile()
        GZIPInputStream(from.inputStream()).use { origin ->
            to.outputStream().use { target ->
                do {
                    val read = origin.read(buffer)
                    if (read == -1)
                        break
                    target.write(buffer, 0, read)
                } while (true)
            }
        }
        from.delete()
        if (to.length() != res.fileSize) {
            printError(
                "Failed to unzip \"${res.fullPath}\" : File size incorrect (Unzipped ${DataSizeUtil.compact(to.length())} / Origin ${
                    DataSizeUtil.compact(
                        res.fileSize
                    )
                })"
            )
            to.delete()
            exitProcess(-1)
        }
        println("Unzipped \"${res.fullPath}\".")
    }
}