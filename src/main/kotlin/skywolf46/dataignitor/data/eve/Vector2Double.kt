package skywolf46.dataignitor.data.eve

class Vector2Double(x: Double, y: Double) : LocationVector<Double>(x, y) {
    val x
        get() = get(0)
    val y
        get() = get(1)

    override fun loadDefaultIndexAlias(): Vector2Double {
        addIndexAlias(
            "x" to 0, "y" to 1
        )
        return this
    }
}