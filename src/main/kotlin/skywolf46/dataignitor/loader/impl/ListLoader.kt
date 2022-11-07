package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

object ListLoader : SchemaDataLoader<YamlWrapper.YamlList> {
    override fun readStream(
        stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo
    ): YamlWrapper.YamlList {
        return if (schema.contains("fixedItemSize")) {
            loadFixedList(stream, schema, errors)
        } else {
            loadVariableSizedList(stream, schema, errors)
        }
    }


    private fun loadFixedList(
        stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo
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

    private fun loadVariableSizedList(
        stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo

    ): YamlWrapper.YamlList {
        val length = if (schema.contains("length")) schema.getInt("length") else (stream.readCInt32())
        println("Length $length")
        // Skip offsets.
        @Suppress("UNUSED_VARIABLE") val listOffsets = Array(length) { stream.readCInt32() }
        println(stream.available())
        val lst = YamlWrapper.YamlList()
        val itemSchema = schema.getSection("itemTypes")!!
        for (x in 0 until length) {
            lst += SchemaDataLoader.represent(stream, itemSchema, errors)
        }
        return lst
    }

}