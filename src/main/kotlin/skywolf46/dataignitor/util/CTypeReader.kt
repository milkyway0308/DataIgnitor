package skywolf46.dataignitor.util

import java.io.DataInputStream

fun DataInputStream.readCInt32(): Int {
    val ch1: Int = read()
    val ch2: Int = read()
    val ch3: Int = read()
    val ch4: Int = read()
    return (ch4 shl 24) or (ch3 shl 16) or (ch2 shl 8) or (ch1)
}