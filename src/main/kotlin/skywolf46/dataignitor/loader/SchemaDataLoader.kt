package skywolf46.dataignitor.loader

import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream
import java.io.InputStream

interface SchemaDataLoader<T : Any> {
    companion object {
        fun <T: Any> of(cls: Any) : SchemaDataLoader<T> {
            return TODO()
        }

        fun <T: Any> of(key: String) : SchemaDataLoader<T> {
            return TODO()
        }

        fun parse(section: YamlReader.YamlSection) : SchemaDataLoader<T> {
            return TODO()
        }
    }
    fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection): T
}