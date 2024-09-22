package voxel

class VoxelChunk(
    var palette: List<Long>,
    val size: Int3
) {
    lateinit var data: ByteArray

    fun rgbi(i: Int): Triple<Int, Int, Int> {
        val hex = palette[i]
        val r = ((hex shr 0) and 0xff).toInt()
        val g = ((hex shr 8) and 0xff).toInt()
        val b = ((hex shr 16) and 0xff).toInt()
        return Triple(r,g,b)
    }
    fun rgbf(i: Int): Triple<Float, Float, Float> {
        val hex = palette[i]
        val r = ((hex shr 0) and 0xff) / 255f
        val g = ((hex shr 8) and 0xff) / 255f
        val b = ((hex shr 16) and 0xff) / 255f
        return Triple(r,g,b)
    }
    operator fun get(i: Int): Int {
        return data[i].toInt() and 0xFF
    }
    fun optimization(): OptVoxel {
        var buffer: ByteArray? = ByteArray(size.dot) { 0 }
        // 000000 -> 上下左右前后
        val fronts = ByteArray(size.dot){ 0 }
        val backs = ByteArray(size.dot){ 0 }
        val lefts = ByteArray(size.dot){ 0 }
        val rights = ByteArray(size.dot){ 0 }
        val tops = ByteArray(size.dot){ 0 }
        val bottoms = ByteArray(size.dot){ 0 }
        var i = 0
//        val s1 = Int3(size.x, size.z, size.y)
        val temp = Int3(size.x, size.y, size.z)
        size.y = temp.z
        size.z = temp.y
        while(i<data.size){
            val x = this[i++]
            val z = temp.y-this[i++]-1
            val y = this[i++]
            val c = data[i++]
            buffer!![size[x,y,z]] = c
        }
        val b0: Byte = 0
        for(x in 0 until size.x) {
            for(y in 0 until size.y){
                for(z in 0 until size.z) {
                    val c = buffer!![size[x,y,z]]
                    if(c==b0)continue
                    if(y>1) {
                        if(buffer[size[x,y-1,z]]==b0){
                            bottoms[size[x,y,z]] = c
                        }
                    }else{
                        bottoms[size[x,y,z]] = c
                    }
                    if(y<size.y-1) {
                        if(buffer[size[x,y+1,z]]==b0){
                            tops[size[x,y,z]] = c
                        }
                    }else{
                        tops[size[x,y,z]] = c
                    }
                    if(x>1) {
                        if(buffer[size[x-1,y,z]]==b0){
                            lefts[size[x,y,z]] = c
                        }
                    }else{
                        lefts[size[x,y,z]] = c
                    }
                    if(x<size.x-1) {
                        if(buffer[size[x+1,y,z]]==b0){
                            rights[size[x,y,z]] = c
                        }
                    }else{
                        rights[size[x,y,z]] = c
                    }
                    if(z>1) {
                        if(buffer[size[x,y,z-1]]==b0){
                            backs[size[x,y,z]] = c
                        }
                    }else{
                        backs[size[x,y,z]] = c
                    }
                    if(z<size.z-1) {
                        if(buffer[size[x,y,z+1]]==b0){
                            fronts[size[x,y,z]] = c
                        }
                    }else{
                        fronts[size[x,y,z]] = c
                    }
                }
            }
        }
        buffer = null
        size.y = temp.y
        size.z = temp.z
        return OptVoxel(
            palette, Int3(size.x,size.z,size.y), fronts, backs, lefts, rights, tops, bottoms
        )
    }
}