package skywolf46.dataignitor.util

object DataSizeUtil {
    fun compact(size: Long): String {
        if (size < 1024)
            return "${size}B"
        return toKilobyte(size / 1024)
    }

    private fun toKilobyte(size: Long): String {
        if (size < 1024)
            return "${size}KB"
        return toMegabyte(size / 1024)
    }

    private fun toMegabyte(size: Long): String {
        if (size < 1024)
            return "${size}MB"
        return toGigabyte(size / 1024)
    }

    private fun toGigabyte(size: Long): String {
        return "${size}GB"
    }
}