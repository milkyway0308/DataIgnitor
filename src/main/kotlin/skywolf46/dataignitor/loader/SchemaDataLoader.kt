package skywolf46.dataignitor.loader

import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.loader.impl.*
import skywolf46.dataignitor.util.YamlReader
import java.io.DataInputStream

interface SchemaDataLoader<T : Any> {
    companion object {
        private val schemas = mutableMapOf<String, SchemaDataLoader<*>>()

        fun registerSchema(schemaDataLoader: SchemaDataLoader<*>, vararg target: String): Companion {
            for (x in target)
                schemas[x] = schemaDataLoader
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> of(key: String): SchemaDataLoader<T> {
            return schemas[key] as SchemaDataLoader<T>
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

        internal fun init() {
            registerSchema(FloatingPointsLoader, "float")
            registerSchema(Vector4Loader, "vector4", "color")
            registerSchema(Vector3Loader, "vector3")
            registerSchema(Vector2Loader, "vector2")
            registerSchema(StringLoader, "string", "resPath", "unicode")
            registerSchema(EnumLoader, "enum")
            registerSchema(BooleanLoader, "bool")
            registerSchema(
                IntLoader,
                "int",
                "typeID",
                "localizationID",
                "npcTag",
                "deploymentType",
                "npcEnemyFleetTypeID",
                "groupBehaviorTreeID",
                "npcCorporationID",
                "spawnTableID",
                "npcFleetCounterTableID",
                "dungeonID",
                "typeListID",
                "npcFleetTypeID",
                "metaGroupID",
                "fsdReference",
                "raceID",
                "marketGroupID",
                "ShipGroupID",
                "certificateTemplateID",
                "factionID"
            )
            registerSchema(ListLoader, "list")
            registerSchema(ObjectLoader, "object")
            registerSchema(DictLoader, "dict")
            registerSchema(UnionLoader, "union")
        }
    }

    fun readStream(stream: DataInputStream, schema: YamlReader.YamlSection, errors: SchemaErrorInfo): T
}