package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.eve.LocationVector
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.DataInputStream

object VectorLoader : SchemaDataLoader<LocationVector<out Number>> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): LocationVector<out Number> {
        return when (schema["type"]!!) {
            "vector4" -> Vector4Loader.readStream(stream, schema, errors)
            "vector3" -> Vector3Loader.readStream(stream, schema, errors)
            "vector2" -> Vector2Loader.readStream(stream, schema, errors)
            else -> throw IllegalStateException("Non-vector stream requested : ${schema["type"]} is not vector")
        }
    }

}