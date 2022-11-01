package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream
import java.util.NoSuchElementException

object EnumLoader : SchemaDataLoader<String> {
    override fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection, errors: SchemaErrorInfo): String {
        val data = when (schema.getInt("size", 1)) {
            1 -> stream.readByte()
            2 -> stream.readShort()
            4 -> stream.readInt()
            else -> throw IllegalStateException(
                "Byte size ${
                    schema.getInt(
                        "size",
                        1
                    )
                } is not supported for Enumeration loader"
            )
        }.toInt()
        return schema.getSection("values")!!.getEntries().find { it.second == data }?.first
            ?: throw NoSuchElementException("No element \"$data\" found in ${schema.nodeName}")
    }
}