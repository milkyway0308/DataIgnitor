package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.eve.*
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCDouble64
import skywolf46.dataignitor.util.readCFloat32
import java.io.DataInputStream

private const val ALIAS = "aliases"

object Vector2Loader : SchemaDataLoader<LocationVector<out Number>> {
    override fun readStream(
        stream: DataInputStream, schema: YamlWrapper.YamlSection, errors: SchemaErrorInfo
    ): LocationVector<out Number> {
        if (schema.contains(ALIAS) && schema.isSection(ALIAS)) {
            return loadFromAlias(stream, schema)
        }
        return loadFromStream(stream, schema).loadDefaultIndexAlias()
    }

    private fun loadFromAlias(stream: DataInputStream, schema: YamlWrapper.YamlSection): LocationVector<*> {
        val locVector = loadFromStream(stream, schema)
        schema.getSection(ALIAS)!!.apply {
            locVector.addIndexAlias(*getKeys(false).map { it to getInt(it) }.toTypedArray())
        }
        return locVector
    }


    private fun loadFromStream(stream: DataInputStream, schema: YamlWrapper.YamlSection): LocationVector<out Number> {
        return if (schema["precision", "single"] == "single") Vector2Float(
            stream.readCFloat32(), stream.readCFloat32()
        )
        else Vector2Double(
            stream.readCDouble64(), stream.readCDouble64()
        )
    }
}