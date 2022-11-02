package skywolf46.dataignitor.loader.impl

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.YamlWrapper
import java.io.DataInputStream

object ObjectLoader : SchemaDataLoader<YamlWrapper.YamlSection> {
    override fun readStream(
        stream: DataInputStream,
        schema: YamlWrapper.YamlSection,
        errors: SchemaErrorInfo
    ): YamlWrapper.YamlSection {
        val map = YamlWrapper.YamlSection()
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
            map[x] = SchemaDataLoader.represent(stream, attributeSchema.getSection(x)!!, errors)
            println("Loaded $x (${map[x]})")
        }
        return map
    }


}