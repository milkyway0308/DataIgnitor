package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.eve.LocationVector
import skywolf46.dataignitor.data.eve.Vector4Double
import skywolf46.dataignitor.data.eve.Vector4Float
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream
import java.io.InputStream

private const val ALIAS = "aliases"

object Vector4Loader : SchemaDataLoader<LocationVector<out Number>> {
    override fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection): LocationVector<out Number> {
        if (schema.contains(ALIAS) && schema.isSection(ALIAS)) {
            return loadFromAlias(stream, schema)
        }
        return loadFromStream(stream, schema).loadDefaultIndexAlias()
    }

    private fun loadFromAlias(stream: DataInputStream, schema: YamlReader.YamlSection): LocationVector<*> {
        val locVector = loadFromStream(stream, schema)
        schema.getSection(ALIAS)!!.apply {
            locVector.addIndexAlias(*getKeys(false).map { it to getInt(it) }.toTypedArray())
        }
        return locVector
    }


    private fun loadFromStream(stream: DataInputStream, schema: YamlReader.YamlSection): LocationVector<out Number> {
        return if (schema["precision", "single"] == "single") Vector4Double(
            stream.readDouble(),
            stream.readDouble(),
            stream.readDouble(),
            stream.readDouble()
        )
        else Vector4Float(
            stream.readFloat(),
            stream.readFloat(),
            stream.readFloat(),
            stream.readFloat()
        )
    }
}