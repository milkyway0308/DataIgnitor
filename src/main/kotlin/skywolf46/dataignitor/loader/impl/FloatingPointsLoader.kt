package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCDouble64
import skywolf46.dataignitor.util.readCFloat32
import java.io.DataInputStream

// I hate weakly typed languages
object FloatingPointsLoader : SchemaDataLoader<Number> {
    // Return value always float or double
    override fun readStream(stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo): Number {
        if (schema["precision"] == "double")
            return stream.readCDouble64()
        return stream.readCFloat32()
    }
}