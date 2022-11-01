package skywolf46.dataignitor.loader

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

interface SchemaDataLoader<T : Any> {
    companion object {
        private val schemas = mutableMapOf<String, SchemaDataLoader<*>>()

        fun <T : Any> of(key: String): SchemaDataLoader<T> {
            return TODO()
        }

        fun <T : Any> parse(
            key: String,
            stream: DataInputStream,
            section: YamlReader.YamlSection,
            errors: SchemaErrorInfo
        ): T {
            return of<T>(key).readStream(stream, section, errors)
        }

        fun <T : Any> represent(stream: DataInputStream, section: YamlReader.YamlSection, errors: SchemaErrorInfo): T {
            return parse(section["type"]!!, stream, section, errors)
        }
    }

    fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection, errors: SchemaErrorInfo): T
}