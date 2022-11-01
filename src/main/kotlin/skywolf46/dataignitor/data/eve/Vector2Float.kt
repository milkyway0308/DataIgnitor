package skywolf46.dataignitor.data.eve

class Vector2Float(x: Float, y: Float) : LocationVector<Float>(x, y) {
    val x
        get() = get(0)
    val y
        get() = get(1)

    override fun loadDefaultIndexAlias(): Vector2Float {
        addIndexAlias(
            "x" to 0, "y" to 1
        )
        return this
    }
}