package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.eve.KeyOffsetData
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

object KeyOffsetDataLoader : SchemaDataLoader<KeyOffsetData> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): KeyOffsetData {
        return KeyOffsetData(stream.readCInt32(), stream.readCInt32())
    }
}