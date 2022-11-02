package skywolf46.dataignitor.util

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class YamlWrapper(stream: InputStream) {
    companion object {
        fun isYamlWritable(data: Any): Boolean {
            return data is Number || data is String || data is YamlSerializable
        }
    }

    val yaml = Yaml()
    val root = YamlSection("", Yaml().load(stream))

    interface YamlExportable {
        fun saveToString(): String
        fun saveToFile(file: File) {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            file.bufferedWriter().use {
                it.write(saveToString())
            }
        }
    }

    interface YamlSerializable {
        fun serialize(): Any
    }

    abstract class MappedYamlSerializable : YamlSerializable {
        override fun serialize(): Any {
            return serializeAsMap()
        }

        protected abstract fun serializeAsMap(): Map<String, Any>
    }


    open class YamlSection internal constructor(
        val nodeName: String = "", private val data: MutableMap<String, Any> = HashMap()
    ) : MappedYamlSerializable(), YamlExportable {
        init {
            fixDataTypes()
        }

        private fun fixDataTypes() {
            data.entries.toList().forEach {
                when (it.value) {
                    is Map<*, *> -> data[it.key] = YamlSection(
                        if (nodeName.isEmpty()) it.key else "$nodeName.${it.key}", it.value as MutableMap<String, Any>
                    )

                    is List<*> -> data[it.key] = YamlList(
                        if (nodeName.isEmpty()) it.key else "$nodeName.${it.key}", it.value as MutableList<Any>
                    )
                }
            }
        }

        operator fun set(key: String, value: Any?): YamlSection {
            if (value == null)
                data.remove(key)
            else {
                if (!isYamlWritable(key))
                    throw IllegalStateException("Cannot serialize ${value.javaClass.name} : Class is not primitive and YamlSerializable")
                data[key] = value
            }
            return this
        }

        operator fun contains(key: String): Boolean {
            return key in data
        }

        fun isList(key: String): Boolean {
            return data[key] is YamlList
        }

        fun isSection(key: String): Boolean {
            return data[key] is YamlSection
        }

        fun isInherited(key: String, cls: Class<*>): Boolean {
            return cls.isInstance(data[key])
        }

        fun isInherited(key: String, cls: KClass<*>): Boolean {
            return isInherited(key, cls.java)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        inline fun <reified T : Any> isInherited(key: String): Boolean {
            return isInherited(key, T::class.java)
        }

        fun isNumber(key: String): Boolean {
            return isInherited<Number>(key)
        }

        fun <T : Any> getRaw(key: String): T? {
            return data[key] as T?
        }

        @JvmOverloads
        operator fun get(key: String, def: String? = null): String? {
            return getRaw<Any>(key)?.toString() ?: def
        }

        @JvmOverloads
        fun getInt(key: String, def: Int = 0): Int {
            return (data[key] as? Number)?.toInt() ?: def
        }

        @JvmOverloads
        fun getFloat(key: String, def: Float = 0f): Float {
            return (data[key] as? Number)?.toFloat() ?: def
        }

        @JvmOverloads
        fun getDouble(key: String, def: Double = 0.0): Double {
            return (data[key] as? Number)?.toDouble() ?: def
        }

        @JvmOverloads
        fun getLong(key: String, def: Long = 0L): Long {
            return (data[key] as? Number)?.toLong() ?: def
        }

        fun getSection(key: String): YamlSection? {
            return data[key] as? YamlSection
        }

        fun getList(key: String): YamlList? {
            return data[key] as? YamlList
        }

        @JvmOverloads
        fun getKeys(deepScan: Boolean = false): List<String> {
            if (!deepScan) return data.keys.toList()
            return data.keys.toList() + data.entries.filter { it.value is YamlSection }
                .map { (it.value as YamlSection).getKeys(true).map { x -> "${it.key}.$x" } }.flatten()
        }

        @JvmOverloads
        fun getEntries(deepScan: Boolean = false): List<Pair<String, Any>> {
            if (!deepScan) return data.entries.map { (x, y) -> x to y }
            return data.entries.map { (x, y) -> x to y } + data.entries.filter { it.value is YamlSection }
                .map { (it.value as YamlSection).getEntries(true).map { x -> "${it.key}.${x.first}" to x.second } }
                .flatten()
        }

        override fun serializeAsMap(): Map<String, Any> {
            return data.mapValues { if (it.value is YamlSerializable) (it.value as YamlSerializable).serialize() else it.value }
        }

        override fun saveToString(): String {
            return Yaml(Representer(DumperOptions().apply {
                this.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
            })).dump(serialize())
        }
    }

    class YamlList internal constructor(
        private val nodeName: String = "", private val list: MutableList<Any> = mutableListOf()
    ) : YamlSerializable, YamlExportable {
        init {
            fixDataTypes()
        }

        operator fun plus(data: Any): YamlList {
            return add(data)
        }

        operator fun plusAssign(data: Any) {
            add(data)
        }

        fun add(data: Any): YamlList {
            if (!isYamlWritable(data))
                throw IllegalStateException("Cannot serialize ${data.javaClass.name} : Class is not primitive and YamlSerializable")
            list.add(data)
            return this
        }

        private fun fixDataTypes() {
            list.indices.forEach {
                when (val next = list[it]) {
                    is Map<*, *> -> list[it] = YamlSection(
                        if (nodeName.isEmpty()) it.toString() else "$nodeName.${it}", next as MutableMap<String, Any>
                    )

                    is List<*> -> list[it] =
                        YamlList(if (nodeName.isEmpty()) it.toString() else "$nodeName.${it}", next as MutableList<Any>)
                }
            }
        }

        fun size(): Int {
            return list.size
        }

        fun <T : Any> get(index: Int): T? {
            return (if (index < 0 || index >= list.size) null else list[index]) as T?
        }

        @JvmOverloads
        fun getInt(index: Int, default: Int = 0): Int {
            return get<Number>(index)?.toInt() ?: default
        }

        @JvmOverloads
        fun getLong(index: Int, default: Long = 0L): Long {
            return get<Number>(index)?.toLong() ?: default
        }

        @JvmOverloads
        fun getDouble(index: Int, default: Double = 0.0): Double {
            return get<Number>(index)?.toDouble() ?: default
        }

        @JvmOverloads
        fun getFloat(index: Int, default: Float = 0f): Float {
            return get<Number>(index)?.toFloat() ?: default
        }

        override fun serialize(): List<Any> {
            return list.map { if (it is YamlSerializable) it.serialize() else it }
        }

        override fun saveToString(): String {
            return Yaml(Representer(DumperOptions().apply {
                this.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
            })).dump(serialize())
        }

    }
}