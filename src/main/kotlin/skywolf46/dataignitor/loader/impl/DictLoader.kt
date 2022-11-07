package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.eve.KeyOffsetData
import skywolf46.dataignitor.data.eve.KeyOffsetWithSizeData
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object DictLoader : SchemaDataLoader<YamlWrapper.YamlSection> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): YamlWrapper.YamlSection {
        val map = YamlWrapper.YamlSection()
        val dataSize = stream.readCInt32() - 4
        val cachedData = ByteArray(dataSize).apply(stream::read)
        val footerSize = stream.readCInt32()
        val dataStream = DataInputStream(ByteArrayInputStream(cachedData, 0, dataSize - footerSize))
        val footerStream = DataInputStream(ByteArrayInputStream(cachedData, dataSize - footerSize, footerSize))
        // isOptimized;
        val isIntKeySchema = schema.getSection("keyTypes")?.get("type") == "int"
        if (isIntKeySchema) {
            val dataAmount = footerStream.readCInt32()
            parseIntegerKeyMap(map, schema, dataStream, footerStream, errors, dataAmount)
        } else {
            parseDataKeyMap(map, schema, dataStream, footerStream, errors)
        }
        return map
    }

    private fun parseIntegerKeyMap(
        map: YamlWrapper.YamlSection,
        configuration: YamlWrapper.YamlSection,
        dataStream: DataInputStream,
        footerStream: DataInputStream,
        errors: SchemaErrorInfo,
        amount: Int
    ) {
        if (configuration.getByContinuity("keyFooter.itemTypes.attributes.size") != null) {
            parseIntegerKeyWithOffsetAndSize(map, configuration, dataStream, footerStream, errors, amount)
        } else {
            parseIntegerKeyWithOffset(map, configuration, dataStream, footerStream, errors, amount)
        }
    }

    private fun parseIntegerKeyWithOffsetAndSize(
        map: YamlWrapper.YamlSection,
        configuration: YamlWrapper.YamlSection,
        dataStream: DataInputStream,
        footerStream: DataInputStream,
        errors: SchemaErrorInfo,
        amount: Int
    ) {
        val valueSchema = configuration.getSection("valueTypes")!!
        val data = mutableListOf<KeyOffsetWithSizeData>()
        for (x in 0 until amount) {
            data += KeyOffsetWithSizeDataLoader.readStream(footerStream, configuration, errors)
        }
        data.sort()
        for (x in 0 until amount) {
            map[data[x].currentKey.toString()] = SchemaDataLoader.represent(dataStream, valueSchema, errors)
        }
    }

    private fun parseIntegerKeyWithOffset(
        map: YamlWrapper.YamlSection,
        configuration: YamlWrapper.YamlSection,
        dataStream: DataInputStream,
        footerStream: DataInputStream,
        errors: SchemaErrorInfo,
        amount: Int
    ) {
        val valueSchema = configuration.getSection("valueTypes")!!

        val data = mutableListOf<KeyOffsetData>()
        for (x in 0 until amount) {
            data += KeyOffsetDataLoader.readStream(footerStream, configuration, errors)
        }
        data.sort()
        for (x in 0 until amount) {
            map[data[x].currentKey.toString()] = SchemaDataLoader.represent(dataStream, valueSchema, errors)
        }
    }

    private fun parseDataKeyMap(
        map: YamlWrapper.YamlSection,
        configuration: YamlWrapper.YamlSection,
        dataStream: DataInputStream,
        footerStream: DataInputStream,
        errors: SchemaErrorInfo,
    ) {
        val footerData = ListLoader.readStream(footerStream, configuration.getSection("keyFooter")!!, errors)
        val valueSchema = configuration.getSection("valueTypes")!!
        for (x in 0 until footerData.size()) {
            val key = footerData.getRaw<Any>(x)!!
            if (key is YamlWrapper.YamlSection) {
                map[key["key"]!!] = SchemaDataLoader.represent(dataStream, valueSchema, errors)
            } else {
                map[key.toString()] = SchemaDataLoader.represent(dataStream, valueSchema, errors)
            }
        }
    }


}