package skywolf46.dataignitor.data.eve

class Vector3Float(x: Float, y: Float, z: Float) : LocationVector<Float>(x, y, z) {
    val x
        get() = get(0)
    val y
        get() = get(1)
    val z
        get() = get(2)

    override fun loadDefaultIndexAlias(): Vector3Float {
        addIndexAlias(
            "x" to 0, "y" to 1, "z" to 2
        )
        return this
    }
}