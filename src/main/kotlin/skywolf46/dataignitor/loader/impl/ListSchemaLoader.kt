package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

class ListSchemaLoader : SchemaDataLoader<List<Any>> {

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
        val length = if (schema.contains("length")) schema.getInt("length") else stream.readInt()
        val itemSchema = schema.getSection("itemTypes")!!
        val itemSize = itemSchema.getInt("size")
        val lst = mutableListOf<Any>()
        for (x in itemSchema.getKeys(false)) {
            if (!itemSchema.isSection(x)) {
                errors.addSchemaError(
                    "${itemSchema.nodeName}.$x",
                    "Cannot represent list item : Schema target is not section"
                )
                continue
            }
            lst += SchemaDataLoader.represent<Any>(stream, itemSchema.getSection(x)!!, errors)
        }
        return lst
    }


}