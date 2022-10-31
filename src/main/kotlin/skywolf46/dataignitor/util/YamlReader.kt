package skywolf46.dataignitor.util

import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class YamlReader(stream: InputStream) {
    val root = YamlSection(Yaml().load(stream))

    open class YamlSection internal constructor(private val data: MutableMap<String, Any> = HashMap()) {
        init {
            fixDataTypes()
        }

        private fun fixDataTypes() {
            data.entries.toList().forEach {
                when (it.value) {
                    is Map<*, *> -> data[it.key] = YamlSection(it.value as MutableMap<String, Any>)
                    is List<*> -> data[it.key] = YamlList(it.value as MutableList<Any>)
                }
            }
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

        operator fun get(key: String): String? {
            return getRaw<Any>(key)?.toString()
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

        @JvmOverloads
        fun getKeys(deepScan: Boolean = false): List<String> {
            if (!deepScan)
                return data.keys.toList()
            return data.keys.toList() + data.entries.filter { it.value is YamlSection }
                .map { (it.value as YamlSection).getKeys(true).map { x -> "${it.key}.$x" } }
                .flatten()
        }
    }

    class YamlList internal constructor(private val data: MutableList<Any> = mutableListOf()) {
        init {
            fixDataTypes()
        }

        private fun fixDataTypes() {
            data.indices.forEach {
                when (val next = data[it]) {
                    is Map<*, *> -> data[it] = YamlSection(next as MutableMap<String, Any>)
                    is List<*> -> data[it] = YamlList(next as MutableList<Any>)
                }
            }
        }

        fun size(): Int {
            return data.size
        }

        fun <T : Any> get(index: Int): T? {
            return (if (index < 0 || index >= data.size) null else data[index]) as T?
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

    }
}