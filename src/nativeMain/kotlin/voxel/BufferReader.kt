package voxel

class BufferReader(val buff: ByteArray) {
    fun i8(offset: Int): Int {
        return this[offset].toInt() and 0xFF
    }
    fun u8(offset: Int): UInt {
        return this[offset].toUInt()
    }
    fun i32(offset: Int, littleEndian: Boolean = false): Int {
        return if(littleEndian) {
            ((this[offset+3].toInt() and 0xFF) shl 24) or
                    ((this[offset+2].toInt() and 0xFF) shl 16) or
                    ((this[offset+1].toInt() and 0xFF) shl 8) or
                    (this[offset].toInt() and 0xFF)
        }else{
            ((this[offset].toInt() and 0xFF) shl 24) or
                    ((this[offset+1].toInt() and 0xFF) shl 16) or
                    ((this[offset+2].toInt() and 0xFF) shl 8) or
                    (this[offset+3].toInt() and 0xFF)
        }
    }
    fun u32(offset: Int, littleEndian: Boolean = false): UInt {
        return if(littleEndian) {
            ((this[offset+3].toUInt() and 0xFFu) shl 24) or
                    ((this[offset+2].toUInt() and 0xFFu) shl 16) or
                    ((this[offset+1].toUInt() and 0xFFu) shl 8) or
                    (this[offset].toUInt() and 0xFFu)
        }else{
            ((this[offset].toUInt() and 0xFFu) shl 24) or
                    ((this[offset+1].toUInt() and 0xFFu) shl 16) or
                    ((this[offset+2].toUInt() and 0xFFu) shl 8) or
                    (this[offset+3].toUInt() and 0xFFu)
        }
    }
    fun l(offset: Int, littleEndian: Boolean = false): Long = u32(offset, littleEndian).toLong()
    fun char(offset: Int): Char {
        return i8(offset).toChar()
    }
    operator fun get(o: Int) = buff[o]
    fun copy(offset: Int, length: Int): ByteArray {
        return buff.copyOfRange(offset, offset+length)
    }
    val size: Int
        get() = buff.size
}
fun ByteArray.chunked(size: Int): List<ByteArray> {
    val result = mutableListOf<ByteArray>()
    var start = 0
    while (start < this.size) {
        val end = minOf(start + size, this.size)
        result.add(this.copyOfRange(start, end))
        start += size
    }
    return result
}

fun to4ByteArray(value: Int): ByteArray {
    return byteArrayOf(
        value.toByte(),
        (value shr 8).toByte(),
        (value shr 16).toByte(),
        (value shr 24).toByte(),
    )
}

fun to4ByteArray(value: Float): ByteArray {
    val bitRep = value.toRawBits()
    return byteArrayOf(
        bitRep.toByte(),
        (bitRep shr 8).toByte(),
        (bitRep shr 16).toByte(),
        (bitRep shr 24).toByte(),
    )
}

fun intFrom4ByteArray(value: ByteArray): Int {
    if(value.size != 4) return 0
    return  (value[3].toInt() and 0xff shl 24) or
            (value[2].toInt() and 0xff shl 16) or
            (value[1].toInt() and 0xff shl 8) or
            (value[0].toInt() and 0xff)

}

fun floatFrom4ByteArray(value: ByteArray): Float {
    if(value.size != 4) return 0.0f
    return Float.fromBits(
        (value[3].toInt() and 0xff shl 24) or
                (value[2].toInt() and 0xff shl 16) or
                (value[1].toInt() and 0xff shl 8) or
                (value[0].toInt() and 0xff)
    )
}
operator fun ByteArray.set(i: Int, v: Float) {
    to4ByteArray(v).let {
        this[i] = it[0]
        this[i+1] = it[1]
        this[i+2] = it[2]
        this[i+3] = it[3]
    }
}
operator fun ByteArray.set(i: Int, v: Int) {
    to4ByteArray(v).let {
        this[i] = it[0]
        this[i+1] = it[1]
        this[i+2] = it[2]
        this[i+3] = it[3]
    }
}