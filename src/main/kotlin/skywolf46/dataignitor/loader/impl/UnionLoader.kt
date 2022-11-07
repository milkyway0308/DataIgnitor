package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

object UnionLoader : SchemaDataLoader<Any> {
    override fun readStream(stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo): Any {
        return SchemaDataLoader.represent(
            stream,
            schema.getSection("optionTypes")!!.getSection(stream.readCInt32().toString())!!,
            errors
        )
    }
}