package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.NumberContainer
import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlReader
import skywolf46.dataignitor.util.readCInt32
import java.io.DataInputStream

const val VALIDATE_MIN = "min"
const val VALIDATE_EXCLUSIVE_MIN = "exclusiveMin"
const val VALIDATE_MAX = "max"
const val VALIDATE_EXCLUSIVE_MAX = "exclusiveMax"

object IntLoader : SchemaDataLoader<NumberContainer<Int>> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlReader.YamlSection,
        errors: SchemaErrorInfo
    ): NumberContainer<Int> {
        val data = stream.readCInt32()
        validate(data, schema, errors)
        return checkUnsigned(data, schema)
    }

    private fun validate(data: Int, schema: YamlReader.YamlSection, errors: SchemaErrorInfo) {
        if (schema.contains(VALIDATE_MIN) && data < schema.getInt(VALIDATE_MIN)) {
            errors.addSchemaError(
                schema.nodeName,
                "Cannot validate int. Min value was \"${schema.getLong(VALIDATE_MIN) - 1}\", but \"$data\" found."
            )
        }
        if (schema.contains(VALIDATE_EXCLUSIVE_MIN) && data <= schema.getInt(VALIDATE_MIN)) {
            errors.addSchemaError(
                schema.nodeName,
                "Cannot validate int. Min value was \"${schema.getInt(VALIDATE_MIN)}\", but \"$data\" found."
            )
        }
        if (schema.contains(VALIDATE_MAX) && data > schema.getInt(VALIDATE_MAX)) {
            errors.addSchemaError(
                schema.nodeName,
                "Cannot validate int. Max value was \"${schema.getLong(VALIDATE_MAX) + 1}\", but \"$data\" found."
            )
        }
        if (schema.contains(VALIDATE_EXCLUSIVE_MAX) && data >= schema.getInt(VALIDATE_EXCLUSIVE_MAX)) {
            errors.addSchemaError(
                schema.nodeName,
                "Cannot validate int. Max value was \"${schema.getList(VALIDATE_EXCLUSIVE_MAX)}\", but \"$data\" found."
            )
        }
    }

    private fun checkUnsigned(data: Int, schema: YamlReader.YamlSection): NumberContainer<Int> {
        if (isUnsignedMin(schema) || isUnsignedExclusiveMin(schema)) {
            return NumberContainer(data, data.toUInt())
        }
        return NumberContainer(data, null)
    }

    private fun isUnsignedMin(schema: YamlReader.YamlSection) =
        (VALIDATE_MIN in schema && schema.getInt(VALIDATE_MIN) >= 0)


    private fun isUnsignedExclusiveMin(schema: YamlReader.YamlSection) =
        (VALIDATE_EXCLUSIVE_MIN in schema && schema.getInt(VALIDATE_EXCLUSIVE_MIN) >= -1)
}