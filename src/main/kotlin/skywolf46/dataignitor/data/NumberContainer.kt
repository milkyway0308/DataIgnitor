package skywolf46.dataignitor.data

data class NumberContainer<T : Number>(val data: T, val unsignedData: Any?) {
    override fun toString(): String {
        return unsignedData?.toString() ?: data.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumberContainer<*>

        if (data != other.data) return false
        if (unsignedData != other.unsignedData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + (unsignedData?.hashCode() ?: 0)
        return result
    }

}