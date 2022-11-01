package skywolf46.dataignitor.loader

import skywolf46.dataignitor.exceptions.InvalidSchemaException
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream
import java.io.InputStream

interface SchemaDataLoader<T : Any> {
    companion object {
        fun <T : Any> of(cls: Class<out T>): SchemaDataLoader<T> {
            return TODO()
        }

        fun <T : Any> of(key: String): SchemaDataLoader<T> {
            return TODO()
        }

        fun <T : Any> parse(deserializeTo: Class<T>, stream: DataInputStream, section: YamlReader.YamlSection): T {
            return of(deserializeTo).readStream(stream, section)
        }

        fun <T : Any> parse(key: String, stream: DataInputStream, section: YamlReader.YamlSection): T {
            return of<T>(key).readStream(stream, section)
        }
    }

    fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection): T
}