package voxel

class Int3(
    var x: Int,
    var y: Int,
    var z: Int
) {
    val dot: Int
        get() = x * y * z
    operator fun get(i: Int, j: Int, k: Int) = i * y * z + j * z + k
    override fun toString(): String {
        return "[$x, $y, $z]"
    }
}