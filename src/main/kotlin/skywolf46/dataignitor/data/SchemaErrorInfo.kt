package skywolf46.dataignitor.data

import java.io.File

class SchemaErrorInfo(val file: File?, val schema: File?) : ArrayList<SchemaErrorInfo.SchemaError>() {
    data class SchemaError(val from: String, val message: String)

    fun addSchemaError(from: String, message: String) {
        add(SchemaError(from, message))
    }
}