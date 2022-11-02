package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.eve.*
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.DataInputStream

private const val ALIAS = "aliases"

object Vector3Loader : SchemaDataLoader<LocationVector<out Number>> {
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
        return if (schema["precision", "single"] == "single") Vector3Float(
            stream.readFloat(), stream.readFloat(), stream.readFloat()
        )
        else Vector3Double(
            stream.readDouble(),
            stream.readDouble(),
            stream.readDouble(),
        )
    }
}