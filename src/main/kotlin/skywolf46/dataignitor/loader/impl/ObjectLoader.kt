package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import skywolf46.dataignitor.util.readCInt32
import skywolf46.dataignitor.util.readCLong32
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object ObjectLoader : SchemaDataLoader<YamlWrapper.YamlSection> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): YamlWrapper.YamlSection {
        val map = YamlWrapper.YamlSection()
        val offsetStart = schema.getInt("endOfFixedSizeData")
        val dataCache = ByteArray(offsetStart).apply(stream::read)
        val optionalTypes =
            if (schema.contains("size")) {
                mutableListOf()
            } else {
                val optionalAttributeField = stream.readCLong32()
                if (schema.contains("optionalValueLookups")) {
                    val lookUp = schema.getSection("optionalValueLookups")!!
                    val offset = schema.getList("attributesWithVariableOffsets")!!
                    mutableListOf<String>().apply {
                        for (x in 0 until offset.size()) {
                            val data = offset[x]!!
                            if (lookUp.contains(data)) {
                                if (optionalAttributeField.and(lookUp.getLong(data)) != 0L) {
                                    this += data
                                }
                            } else {
                                this += data
                            }
                        }
                    }
                } else {
                    mutableListOf<String>().apply {
                        val lst = schema.getList("attributesWithVariableOffsets")!!
                        for (x in 0 until lst.size())
                            this += lst.getRaw<Any>(x).toString()
                    }
                }
            }
        val attributeMap = optionalTypes.associateWith {
            stream.readCInt32()
        }
        val constantAttributeOffsets = schema.getSection("constantAttributeOffsets")!!
        val dataStream = DataInputStream(ByteArrayInputStream(dataCache))
        val attributeSchema = schema.getSection("attributes")!!
        // Skip all attributes, we don't need it.
        for (x in attributeSchema.getKeys()) {
            if (!attributeSchema.isSection(x)) {
                errors.addSchemaError(
                    "${attributeSchema.nodeName}.$x",
                    "Cannot represent object item : Schema target is not section"
                )
                continue
            }
            if (constantAttributeOffsets.contains(x)) {
                map[x] = SchemaDataLoader.represent(dataStream, attributeSchema.getSection(x)!!, errors)
            } else if (attributeMap.containsKey(x)) {
                map[x] = SchemaDataLoader.represent(stream, attributeSchema.getSection(x)!!, errors)
            } else {
                if (attributeSchema.getSection(x)?.contains("default") == true) {
                    map[x] = attributeSchema.getSection(x)!!["default"]
                }
                continue
            }
        }
        return map
    }


}