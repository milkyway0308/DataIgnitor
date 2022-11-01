package skywolf46.dataignitor.loader

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

interface SchemaDataLoader<T : Any> {
    companion object {
        fun <T : Any> of(cls: Class<out T>): SchemaDataLoader<T> {
            return TODO()
        }

        fun <T : Any> of(key: String): SchemaDataLoader<T> {
            return TODO()
        }

        fun <T : Any> parse(
            deserializeTo: Class<T>,
            stream: DataInputStream,
            section: YamlReader.YamlSection,
            errors: SchemaErrorInfo
        ): T {
            return of(deserializeTo).readStream(stream, section, errors)
        }

        fun <T : Any> parse(
            key: String,
            stream: DataInputStream,
            section: YamlReader.YamlSection,
            errors: SchemaErrorInfo
        ): T {
            return of<T>(key).readStream(stream, section, errors)
        }
    }

    fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection, errors: SchemaErrorInfo): T
}