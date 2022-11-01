package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.eve.*
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

private const val ALIAS = "aliases"

object Vector2Loader : SchemaDataLoader<LocationVector<out Number>> {
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
        return if (schema["precision", "single"] == "single") Vector2Double(
            stream.readDouble(),
            stream.readDouble()
        )
        else Vector2Float(
            stream.readFloat(),
            stream.readFloat()
        )
    }
}