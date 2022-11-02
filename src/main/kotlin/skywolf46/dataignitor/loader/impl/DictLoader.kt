package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.DataInputStream

object DictLoader : SchemaDataLoader<Map<String, Any>> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val dataSize = stream.readInt()
        val footerSize = stream.readInt()
        val valueSchema = schema.getSection("valueTypes")!!
        for (x in valueSchema.getKeys()) {
            if (!valueSchema.isSection(x)) {

                continue
            }
            map[x] = SchemaDataLoader.represent(stream, valueSchema.getSection(x)!!, errors)
        }

        // Data used for pyFSD implementation. Ignore it.
//        val isFooterOptimized = schema.getSection("keyTypes")!!["type"] == "int"
//        val footer =
//            ListSchemaLoader.readStream(stream, schema.getSection("keyFooter")!!, errors) as List<Map<String, Int>>
        // Ignore footer.
        val data = ByteArray(dataSize).apply(stream::read)

        return map
    }

}