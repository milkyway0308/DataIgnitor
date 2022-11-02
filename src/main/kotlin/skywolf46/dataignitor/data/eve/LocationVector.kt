package skywolf46.dataignitor.data.eve

import skywolf46.dataignitor.util.YamlWrapper
import java.util.NoSuchElementException

@Suppress("UNCHECKED_CAST")
abstract class LocationVector<T : Number>(val size: Int, vararg data: T) : YamlWrapper.MappedYamlSerializable() {
    private val dimension = Array(size) { data.getOrNull(size) ?: 0 }
    private val indexKey = mutableListOf<Pair<String, Int>>()
    private val priority = mutableListOf<Int>()

    constructor(vararg data: T) : this(data.size, *data)

    fun get(index: Int) = dimension[index] as T

    fun get(key: String): T {
        return dimension[indexKey.filter { it.first == key }.getOrNull(0)?.second
            ?: throw NoSuchElementException("Element $key is not found in this ${javaClass.name} instance")] as T
    }

    fun addIndexAlias(vararg key: Pair<String, Int>): LocationVector<T> {
        indexKey += key
        return this
    }


    abstract fun loadDefaultIndexAlias(): LocationVector<T>

    override fun serializeAsMap(): Map<String, Any> {
        return dimension.mapIndexed { it, value -> it to value }
            .associate { entry ->
                (indexKey.filter { entry.first == it.second }.getOrNull(0)?.first
                    ?: "unspecified_${entry.first}") to entry.second
            }
    }
}