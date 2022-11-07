package skywolf46.dataignitor.data.eve

open class KeyOffsetData(val currentKey: Int, val offset: Int) : Comparable<KeyOffsetData> {
    override fun compareTo(other: KeyOffsetData): Int {
        return offset.compareTo(other.offset)
    }
}