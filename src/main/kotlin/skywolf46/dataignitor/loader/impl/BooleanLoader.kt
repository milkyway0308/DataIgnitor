package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.DataInputStream

object BooleanLoader : SchemaDataLoader<Boolean> {
    override fun readStream(stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo): Boolean {
        return stream.readByte() == 255.toByte()
    }
}