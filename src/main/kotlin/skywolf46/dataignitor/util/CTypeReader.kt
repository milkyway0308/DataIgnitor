package skywolf46.dataignitor.util

import java.io.DataInputStream

fun DataInputStream.readCInt32(): Int {
    val ch1: Int = read()
    val ch2: Int = read()
    val ch3: Int = read()
    val ch4: Int = read()
    return (ch4 shl 24) or (ch3 shl 16) or (ch2 shl 8) or (ch1)
}

fun DataInputStream.readCFloat32() : Float {
    return java.lang.Float.intBitsToFloat(readCInt32())
}

fun DataInputStream.readCDouble32() : Double {
    return java.lang.Double.longBitsToDouble(readCLong32())
}
fun DataInputStream.readCLong32(): Long {
    val arr = ByteArray(8).apply(this::read)
    return (arr[7].toLong() shl 56) or
            ((arr[6].toLong() and 255) shl 48) or
            ((arr[5].toLong() and 255) shl 40) or
            ((arr[4].toLong() and 255) shl 32) or
            ((arr[3].toLong() and 255) shl 24) or
            (arr[2].toLong() and 255 shl 16) or
            (arr[1].toLong() and 255 shl 8) or
            (arr[0].toLong() and 255 shl 0)
}

