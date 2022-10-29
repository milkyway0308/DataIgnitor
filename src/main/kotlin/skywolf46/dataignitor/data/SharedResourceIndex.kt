package skywolf46.dataignitor.data

import java.io.File

data class SharedResourceIndex(
    val name: String,
    val fullPath: String,
    val resourcePath: String,
    val fileHash: String,
    val fileSize: Long,
    val compressedFileSize: Long
) {
    companion object {
        fun fromIndexFileRow(row: String): SharedResourceIndex {
            val data = row.split(",")
            if (data.size != 5) {
                throw IllegalStateException("Failed to parse index row : Index row not have 5 column")
            }
            val name = data[0].substring(data[0].lastIndexOf("/").coerceAtLeast(0))
            return SharedResourceIndex(
                name,
                data[0],
                data[1],
                data[2],
                data[3].toLongOrNull()
                    ?: throw IllegalStateException("Failed to parse index row : File size of ${data[0]} is not number"),
                data[4].toLongOrNull()
                    ?: throw IllegalStateException("Failed to parse index row : Compressed size of ${data[0]} is not number"),
            )
        }
    }

    fun toSubDirectory(parent: File, suffix : String = ""): File {
        if (fullPath.startsWith("res:/"))
            return File(parent, fullPath.substring(5) + suffix)
        return File(parent, fullPath + suffix)
    }
}