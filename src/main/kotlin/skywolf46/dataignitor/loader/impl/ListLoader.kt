package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

object ListLoader : SchemaDataLoader<List<Any>> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlReader.YamlSection,
        errors: SchemaErrorInfo
    ): List<Any> {
        return loadFixedList(stream, schema, errors)
    }

    private fun loadFixedList(
        stream: DataInputStream,
        schema: YamlReader.YamlSection,
        errors: SchemaErrorInfo
    ): List<Any> {
        val isFixedLength = schema.contains("length")
        val length = if (schema.contains("length")) schema.getInt("length").toLong() else (stream.readCInt32())
        val itemSchema = schema.getSection("itemTypes")!!
        val itemSize = itemSchema.getInt("size")
        val lst = mutableListOf<Any>()
        for (x in 0L until itemSize) {
            lst += SchemaDataLoader.represent<Any>(stream, itemSchema, errors)
        }
        println(lst)
        return lst
    }


}