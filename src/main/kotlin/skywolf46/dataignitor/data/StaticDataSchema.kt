package skywolf46.dataignitor.data

import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.InputStream

open class StaticDataSchema {
    companion object {
        fun fromSchemaYaml(yaml: YamlWrapper.YamlSection, fsdStream: InputStream, errorInfo: SchemaErrorInfo): Any {
            return SchemaDataLoader.represent<Any>(DataInputStream(fsdStream), yaml, errorInfo)
        }

        fun fromFileStream(stream: InputStream, errorInfo: SchemaErrorInfo): Any {
            val readStream = DataInputStream(stream)

            stream.mark(Integer.MAX_VALUE)
            val byteBuffer = ByteArray(readStream.readInt())
            stream.read(byteBuffer)
            return fromSchemaYaml(YamlWrapper(ByteArrayInputStream(byteBuffer)).root, stream, errorInfo)
        }
    }


    class IndexLoaderSchema : StaticDataSchema() {

    }
}