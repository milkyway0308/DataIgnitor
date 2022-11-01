package skywolf46.dataignitor.data.eve

class Vector4Float(w: Float, x: Float, y: Float, z: Float) : LocationVector<Float>(w, x, y, z) {
    val w
        get() = get(0)
    val x
        get() = get(1)
    val y
        get() = get(2)
    val z
        get() = get(3)

    override fun loadDefaultIndexAlias(): Vector4Float {
        addIndexAlias(
            "w" to 0, "x" to 1, "y" to 2, "z" to 3
        )
        return this
    }
}