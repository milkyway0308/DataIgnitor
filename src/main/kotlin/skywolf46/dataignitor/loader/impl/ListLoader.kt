package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

object ListLoader : SchemaDataLoader<YamlWrapper.YamlList> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): YamlWrapper.YamlList {
        return loadFixedList(stream, schema, errors)
    }

    private fun loadFixedList(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): YamlWrapper.YamlList {
        val isFixedLength = schema.contains("length")
        val length = if (schema.contains("length")) schema.getInt("length") else (stream.readCInt32())
        val itemSchema = schema.getSection("itemTypes")!!
        val itemSize = itemSchema.getInt("size")
        val lst = YamlWrapper.YamlList()
        for (x in 0 until length) {
            lst += SchemaDataLoader.represent(stream, itemSchema, errors)
        }
        return lst
    }


}