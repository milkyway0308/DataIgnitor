package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

object StringLoader : SchemaDataLoader<String> {
    override fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection, errors: SchemaErrorInfo): String {
        val strSize = stream.readInt()
        return String(ByteArray(strSize).apply(stream::read))
    }
}