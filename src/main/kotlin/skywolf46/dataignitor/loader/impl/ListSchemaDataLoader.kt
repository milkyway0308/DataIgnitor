package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.InputStream

class ListSchemaDataLoader : SchemaDataLoader<List<Any>> {
    override fun readStream(stream: InputStream, schema: YamlReader.YamlSection): List<Any> {
        if ("fixedItemSize" in schema) {
            40.toULong()
        }
        TODO("Not yet implemented")
    }

    private fun loadFixedList(stream: InputStream, schema: YamlReader.YamlSection): List<Any> {

    }
}