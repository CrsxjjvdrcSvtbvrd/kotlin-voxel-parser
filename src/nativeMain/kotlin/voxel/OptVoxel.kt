package voxel

import kotlin.math.roundToInt

class OptVoxel (
    var palette: List<Long>,
    val size: Int3,
    val fronts: ByteArray,
    val backs:  ByteArray,
    val lefts: ByteArray,
    val rights: ByteArray,
    val tops: ByteArray,
    val bottoms: ByteArray
) {
    fun rgbi(i: Int): Triple<Int, Int, Int> {
        val hex = palette[i]
        val r = ((hex shr 0) and 0xff).toInt()
        val g = ((hex shr 8) and 0xff).toInt()
        val b = ((hex shr 16) and 0xff).toInt()
        return Triple(r,g,b)
    }
    fun buildMesh(): Pair<FloatArray, IntArray> {
        val vertices = mutableListOf<Float>()
        val s1 = Int3(size.x+1,size.y+1,size.z+1)
        val vertIndex = mutableMapOf<String, Int>()
        val indices = mutableListOf<Int>()
        val b0: Byte = 0
        var nv = 0
        for(x in 0 until size.x) {
            for (y in 0 until size.y) {
                for (z in 0 until size.z) {
                    if(tops[size[x,y,z]]!=b0) {
                        val c = tops[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y+1,z+1]
                        val p4 = s1[x+1,y+1,z+1]
                        if(vertIndex[0,p1,c]==-1){
                            vertices.add(x,y+1,z,0,1,0,r,g,b,1f)
                            vertIndex[0,p1,c] = nv++
                        }
                        if(vertIndex[0,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,1,0,r,g,b,1f)
                            vertIndex[0,p2,c] = nv++
                        }
                        if(vertIndex[0,p3,c]==-1){
                            vertices.add(x,y+1,z+1,0,1,0,r,g,b,1f)
                            vertIndex[0,p3,c] = nv++
                        }
                        if(vertIndex[0,p4,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,1,0,r,g,b,1f)
                            vertIndex[0,p4,c] = nv++
                        }
                        indices.add(vertIndex[0,p1,c])
                        indices.add(vertIndex[0,p3,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p3,c])
                        indices.add(vertIndex[0,p4,c])
                    }
                    if(bottoms[size[x,y,z]]!=b0) {
                        val c = bottoms[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y,z]
                        val p2 = s1[x+1,y,z]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[1,p1,c]==-1){
                            vertices.add(x,y,z,0,-1,0,r,g,b,1f)
                            vertIndex[1,p1,c] = nv++
                        }
                        if(vertIndex[1,p2,c]==-1){
                            vertices.add(x+1,y,z,0,-1,0,r,g,b,1f)
                            vertIndex[1,p2,c] = nv++
                        }
                        if(vertIndex[1,p3,c]==-1){
                            vertices.add(x,y,z+1,0,-1,0,r,g,b,1f)
                            vertIndex[1,p3,c] = nv++
                        }
                        if(vertIndex[1,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,-1,0,r,g,b,1f)
                            vertIndex[1,p4,c] = nv++
                        }
                        indices.add(vertIndex[1,p1,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p3,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p4,c])
                        indices.add(vertIndex[1,p3,c])
                    }
                    if(lefts[size[x,y,z]]!=b0) {
                        val c = lefts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x,y+1,z+1]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x,y,z+1]
                        if(vertIndex[2,p1,c]==-1){
                            vertices.add(x,y+1,z,-1,0,0,r,g,b,1f)
                            vertIndex[2,p1,c] = nv++
                        }
                        if(vertIndex[2,p2,c]==-1){
                            vertices.add(x,y+1,z+1,-1,0,0,r,g,b,1f)
                            vertIndex[2,p2,c] = nv++
                        }
                        if(vertIndex[2,p3,c]==-1){
                            vertices.add(x,y,z,-1,0,0,r,g,b,1f)
                            vertIndex[2,p3,c] = nv++
                        }
                        if(vertIndex[2,p4,c]==-1){
                            vertices.add(x,y,z+1,-1,0,0,r,g,b,1f)
                            vertIndex[2,p4,c] = nv++
                        }
                        indices.add(vertIndex[2,p1,c])
                        indices.add(vertIndex[2,p3,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p3,c])
                        indices.add(vertIndex[2,p4,c])
                    }
                    if(rights[size[x,y,z]]!=b0) {
                        val c = rights[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x+1,y+1,z]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x+1,y,z]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[3,p1,c]==-1){
                            vertices.add(x+1,y+1,z,1,0,0,r,g,b,1f)
                            vertIndex[3,p1,c] = nv++
                        }
                        if(vertIndex[3,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,1,0,0,r,g,b,1f)
                            vertIndex[3,p2,c] = nv++
                        }
                        if(vertIndex[3,p3,c]==-1){
                            vertices.add(x+1,y,z,1,0,0,r,g,b,1f)
                            vertIndex[3,p3,c] = nv++
                        }
                        if(vertIndex[3,p4,c]==-1){
                            vertices.add(x+1,y,z+1,1,0,0,r,g,b,1f)
                            vertIndex[3,p4,c] = nv++
                        }
                        indices.add(vertIndex[3,p1,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p3,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p4,c])
                        indices.add(vertIndex[3,p3,c])
                    }
                    if(fronts[size[x,y,z]]!=b0) {
                        val c = fronts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z+1]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[4,p1,c]==-1){
                            vertices.add(x,y+1,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p1,c] = nv++
                        }
                        if(vertIndex[4,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p2,c] = nv++
                        }
                        if(vertIndex[4,p3,c]==-1){
                            vertices.add(x,y,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p3,c] = nv++
                        }
                        if(vertIndex[4,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p4,c] = nv++
                        }
                        indices.add(vertIndex[4,p1,c])
                        indices.add(vertIndex[4,p3,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p3,c])
                        indices.add(vertIndex[4,p4,c])
                    }
                    if(backs[size[x,y,z]]!=b0) {
                        val c = backs[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x+1,y,z]
                        if(vertIndex[5,p1,c]==-1){
                            vertices.add(x,y+1,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p1,c] = nv++
                        }
                        if(vertIndex[5,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p2,c] = nv++
                        }
                        if(vertIndex[5,p3,c]==-1){
                            vertices.add(x,y,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p3,c] = nv++
                        }
                        if(vertIndex[5,p4,c]==-1){
                            vertices.add(x+1,y,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p4,c] = nv++
                        }
                        indices.add(vertIndex[5,p1,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p3,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p4,c])
                        indices.add(vertIndex[5,p3,c])
                    }
                }
            }
        }
        println("顶点数:${vertices.size},索引数:${indices.size},点数:${vertices.size/10},面数:${indices.size/3}")
        return vertices.toFloatArray() to indices.toIntArray()
    }
    fun rgbf(i: Int): Triple<Float, Float, Float> {
        val hex = palette[i]
        val r = ((hex shr 0) and 0xff) / 255f
        val g = ((hex shr 8) and 0xff) / 255f
        val b = ((hex shr 16) and 0xff) / 255f
        return Triple(r,g,b)
    }
    fun exportPly(binary: Boolean = false): ByteArray {
        val vertices = mutableListOf<Float>()
        val s1 = Int3(size.x+1,size.y+1,size.z+1)
        val vertIndex = mutableMapOf<String, Int>()
        val indices = mutableListOf<Int>()
        val b0: Byte = 0
        var nv = 0
        for(x in 0 until size.x) {
            for (y in 0 until size.y) {
                for (z in 0 until size.z) {
                    if(tops[size[x,y,z]]!=b0) {
                        val c = tops[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y+1,z+1]
                        val p4 = s1[x+1,y+1,z+1]
                        if(vertIndex[0,p1,c]==-1){
                            vertices.add(x,y+1,z,0,1,0,r,g,b,255)
                            vertIndex[0,p1,c] = nv++
                        }
                        if(vertIndex[0,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,1,0,r,g,b,255)
                            vertIndex[0,p2,c] = nv++
                        }
                        if(vertIndex[0,p3,c]==-1){
                            vertices.add(x,y+1,z+1,0,1,0,r,g,b,255)
                            vertIndex[0,p3,c] = nv++
                        }
                        if(vertIndex[0,p4,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,1,0,r,g,b,255)
                            vertIndex[0,p4,c] = nv++
                        }
                        indices.add(vertIndex[0,p1,c])
                        indices.add(vertIndex[0,p3,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p3,c])
                        indices.add(vertIndex[0,p4,c])
                    }
                    if(bottoms[size[x,y,z]]!=b0) {
                        val c = bottoms[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x,y,z]
                        val p2 = s1[x+1,y,z]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[1,p1,c]==-1){
                            vertices.add(x,y,z,0,-1,0,r,g,b,255)
                            vertIndex[1,p1,c] = nv++
                        }
                        if(vertIndex[1,p2,c]==-1){
                            vertices.add(x+1,y,z,0,-1,0,r,g,b,255)
                            vertIndex[1,p2,c] = nv++
                        }
                        if(vertIndex[1,p3,c]==-1){
                            vertices.add(x,y,z+1,0,-1,0,r,g,b,255)
                            vertIndex[1,p3,c] = nv++
                        }
                        if(vertIndex[1,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,-1,0,r,g,b,255)
                            vertIndex[1,p4,c] = nv++
                        }
                        indices.add(vertIndex[1,p1,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p3,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p4,c])
                        indices.add(vertIndex[1,p3,c])
                    }
                    if(lefts[size[x,y,z]]!=b0) {
                        val c = lefts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x,y+1,z+1]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x,y,z+1]
                        if(vertIndex[2,p1,c]==-1){
                            vertices.add(x,y+1,z,-1,0,0,r,g,b,255)
                            vertIndex[2,p1,c] = nv++
                        }
                        if(vertIndex[2,p2,c]==-1){
                            vertices.add(x,y+1,z+1,-1,0,0,r,g,b,255)
                            vertIndex[2,p2,c] = nv++
                        }
                        if(vertIndex[2,p3,c]==-1){
                            vertices.add(x,y,z,-1,0,0,r,g,b,255)
                            vertIndex[2,p3,c] = nv++
                        }
                        if(vertIndex[2,p4,c]==-1){
                            vertices.add(x,y,z+1,-1,0,0,r,g,b,255)
                            vertIndex[2,p4,c] = nv++
                        }
                        indices.add(vertIndex[2,p1,c])
                        indices.add(vertIndex[2,p3,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p3,c])
                        indices.add(vertIndex[2,p4,c])
                    }
                    if(rights[size[x,y,z]]!=b0) {
                        val c = rights[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x+1,y+1,z]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x+1,y,z]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[3,p1,c]==-1){
                            vertices.add(x+1,y+1,z,1,0,0,r,g,b,255)
                            vertIndex[3,p1,c] = nv++
                        }
                        if(vertIndex[3,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,1,0,0,r,g,b,255)
                            vertIndex[3,p2,c] = nv++
                        }
                        if(vertIndex[3,p3,c]==-1){
                            vertices.add(x+1,y,z,1,0,0,r,g,b,255)
                            vertIndex[3,p3,c] = nv++
                        }
                        if(vertIndex[3,p4,c]==-1){
                            vertices.add(x+1,y,z+1,1,0,0,r,g,b,255)
                            vertIndex[3,p4,c] = nv++
                        }
                        indices.add(vertIndex[3,p1,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p3,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p4,c])
                        indices.add(vertIndex[3,p3,c])
                    }
                    if(fronts[size[x,y,z]]!=b0) {
                        val c = fronts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x,y+1,z+1]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[4,p1,c]==-1){
                            vertices.add(x,y+1,z+1,0,0,1,r,g,b,255)
                            vertIndex[4,p1,c] = nv++
                        }
                        if(vertIndex[4,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,0,1,r,g,b,255)
                            vertIndex[4,p2,c] = nv++
                        }
                        if(vertIndex[4,p3,c]==-1){
                            vertices.add(x,y,z+1,0,0,1,r,g,b,255)
                            vertIndex[4,p3,c] = nv++
                        }
                        if(vertIndex[4,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,0,1,r,g,b,255)
                            vertIndex[4,p4,c] = nv++
                        }
                        indices.add(vertIndex[4,p1,c])
                        indices.add(vertIndex[4,p3,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p3,c])
                        indices.add(vertIndex[4,p4,c])
                    }
                    if(backs[size[x,y,z]]!=b0) {
                        val c = backs[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbi(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x+1,y,z]
                        if(vertIndex[5,p1,c]==-1){
                            vertices.add(x,y+1,z,0,0,-1,r,g,b,255)
                            vertIndex[5,p1,c] = nv++
                        }
                        if(vertIndex[5,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,0,-1,r,g,b,255)
                            vertIndex[5,p2,c] = nv++
                        }
                        if(vertIndex[5,p3,c]==-1){
                            vertices.add(x,y,z,0,0,-1,r,g,b,255)
                            vertIndex[5,p3,c] = nv++
                        }
                        if(vertIndex[5,p4,c]==-1){
                            vertices.add(x+1,y,z,0,0,-1,r,g,b,255)
                            vertIndex[5,p4,c] = nv++
                        }
                        indices.add(vertIndex[5,p1,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p3,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p4,c])
                        indices.add(vertIndex[5,p3,c])
                    }
                }
            }
        }
        //顶点数:18800,索引数:6738,点数:1880,面数:1123
        println("顶点数:${vertices.size},索引数:${indices.size},点数:${vertices.size/10},面数:${indices.size/3}")
        if(!binary) {
            val header = StringBuilder()
            header
                .appendLine("ply")
                .appendLine("format ascii 1.0")
                .appendLine("element vertex ${vertices.size / 10}")
                .appendLine("property float x")
                .appendLine("property float y")
                .appendLine("property float z")
                .appendLine("property float nx")
                .appendLine("property float ny")
                .appendLine("property float nz")
                .appendLine("property uchar red")
                .appendLine("property uchar green")
                .appendLine("property uchar blue")
                .appendLine("property uchar alpha")
                .appendLine("element face ${indices.size / 3}")
                .appendLine("property list uchar int vertex_indices")
                .appendLine("end_header")
            for (p in vertices.indices step 10) {
                header.append(vertices[p]).append(' ')
                    .append(vertices[p + 1]).append(' ')
                    .append(vertices[p + 2]).append(' ')
                    .append(vertices[p + 3]).append(' ')
                    .append(vertices[p + 4]).append(' ')
                    .append(vertices[p + 5]).append(' ')
                    .append(vertices[p + 6].roundToInt()).append(' ')
                    .append(vertices[p + 7].roundToInt()).append(' ')
                    .append(vertices[p + 8].roundToInt()).append(' ')
                    .append(vertices[p + 9].roundToInt()).appendLine()
            }
            for (p in indices.indices step 3) {
                header.append(3).append(' ').append(indices[p]).append(' ').append(indices[p + 1]).append(' ')
                    .append(indices[p + 2]).appendLine()
            }
            return header.toString().encodeToByteArray()
        }else{
            val header = StringBuilder()
            header
                .appendLine("ply")
                .appendLine("format binary_little_endian 1.0")
                .appendLine("element vertex ${vertices.size / 10}")
                .appendLine("property float x")
                .appendLine("property float y")
                .appendLine("property float z")
                .appendLine("property float nx")
                .appendLine("property float ny")
                .appendLine("property float nz")
                .appendLine("property uchar red")
                .appendLine("property uchar green")
                .appendLine("property uchar blue")
                .appendLine("property uchar alpha")
                .appendLine("element face ${indices.size / 3}")
                .appendLine("property list uchar int vertex_indices")
                .appendLine("end_header")
            val buffheader = header.toString().encodeToByteArray()
            val buffsize = (28 * vertices.size / 10) + (13 * indices.size / 3)
            println(buffsize)
            val buff = ByteArray(buffheader.size+buffsize)
            buffheader.copyInto(buff)
            var index = buffheader.size
            for (p in vertices.indices step 10) {
                buff[index] = vertices[p]
                index += 4
                buff[index] = vertices[p+1]
                index += 4
                buff[index] = vertices[p+2]
                index += 4
                buff[index] = vertices[p+3]
                index += 4
                buff[index] = vertices[p+4]
                index += 4
                buff[index] = vertices[p+5]
                index += 4

                buff[index] = vertices[p+6].roundToInt().toByte()
                index ++
                buff[index] = vertices[p+7].roundToInt().toByte()
                index ++
                buff[index] = vertices[p+8].roundToInt().toByte()
                index ++
                buff[index] = vertices[p+9].roundToInt().toByte()
                index ++
            }
            for (p in indices.indices step 3) {
                buff[index] = (3).toByte()
                index++
                buff[index] = indices[p]
                index+=4
                buff[index] = indices[p+1]
                index+=4
                buff[index] = indices[p+2]
                index+=4
            }
            return buff
        }
    }


    fun buildArray(): Pair<FloatArray, IntArray> {
        val vertices = mutableListOf<Float>()
        val s1 = Int3(size.x+1,size.y+1,size.z+1)
        val vertIndex = mutableMapOf<String, Int>()
        val indices = mutableListOf<Int>()
        val b0: Byte = 0
        var nv = 0
        for(x in 0 until size.x) {
            for (y in 0 until size.y) {
                for (z in 0 until size.z) {
                    if(tops[size[x,y,z]]!=b0) {
                        val c = tops[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y+1,z+1]
                        val p4 = s1[x+1,y+1,z+1]
                        if(vertIndex[0,p1,c]==-1){
                            vertices.add(x,y+1,z,0,1,0,r,g,b,1f)
                            vertIndex[0,p1,c] = nv++
                        }
                        if(vertIndex[0,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,1,0,r,g,b,1f)
                            vertIndex[0,p2,c] = nv++
                        }
                        if(vertIndex[0,p3,c]==-1){
                            vertices.add(x,y+1,z+1,0,1,0,r,g,b,1f)
                            vertIndex[0,p3,c] = nv++
                        }
                        if(vertIndex[0,p4,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,1,0,r,g,b,1f)
                            vertIndex[0,p4,c] = nv++
                        }
                        indices.add(vertIndex[0,p1,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p3,c])
                        indices.add(vertIndex[0,p2,c])
                        indices.add(vertIndex[0,p4,c])
                        indices.add(vertIndex[0,p3,c])
                    }
                    if(bottoms[size[x,y,z]]!=b0) {
                        val c = bottoms[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y,z]
                        val p2 = s1[x+1,y,z]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[1,p1,c]==-1){
                            vertices.add(x,y,z,0,-1,0,r,g,b,1f)
                            vertIndex[1,p1,c] = nv++
                        }
                        if(vertIndex[1,p2,c]==-1){
                            vertices.add(x+1,y,z,0,-1,0,r,g,b,1f)
                            vertIndex[1,p2,c] = nv++
                        }
                        if(vertIndex[1,p3,c]==-1){
                            vertices.add(x,y,z+1,0,-1,0,r,g,b,1f)
                            vertIndex[1,p3,c] = nv++
                        }
                        if(vertIndex[1,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,-1,0,r,g,b,1f)
                            vertIndex[1,p4,c] = nv++
                        }
                        indices.add(vertIndex[1,p1,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p3,c])
                        indices.add(vertIndex[1,p2,c])
                        indices.add(vertIndex[1,p4,c])
                        indices.add(vertIndex[1,p3,c])
                    }
                    if(lefts[size[x,y,z]]!=b0) {
                        val c = lefts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x,y+1,z+1]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x,y,z+1]
                        if(vertIndex[2,p1,c]==-1){
                            vertices.add(x,y+1,z,-1,0,0,r,g,b,1f)
                            vertIndex[2,p1,c] = nv++
                        }
                        if(vertIndex[2,p2,c]==-1){
                            vertices.add(x,y+1,z+1,-1,0,0,r,g,b,1f)
                            vertIndex[2,p2,c] = nv++
                        }
                        if(vertIndex[2,p3,c]==-1){
                            vertices.add(x,y,z,-1,0,0,r,g,b,1f)
                            vertIndex[2,p3,c] = nv++
                        }
                        if(vertIndex[2,p4,c]==-1){
                            vertices.add(x,y,z+1,-1,0,0,r,g,b,1f)
                            vertIndex[2,p4,c] = nv++
                        }
                        indices.add(vertIndex[2,p1,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p3,c])
                        indices.add(vertIndex[2,p2,c])
                        indices.add(vertIndex[2,p4,c])
                        indices.add(vertIndex[2,p3,c])
                    }
                    if(rights[size[x,y,z]]!=b0) {
                        val c = rights[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x+1,y+1,z]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x+1,y,z]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[3,p1,c]==-1){
                            vertices.add(x+1,y+1,z,1,0,0,r,g,b,1f)
                            vertIndex[3,p1,c] = nv++
                        }
                        if(vertIndex[3,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,1,0,0,r,g,b,1f)
                            vertIndex[3,p2,c] = nv++
                        }
                        if(vertIndex[3,p3,c]==-1){
                            vertices.add(x+1,y,z,1,0,0,r,g,b,1f)
                            vertIndex[3,p3,c] = nv++
                        }
                        if(vertIndex[3,p4,c]==-1){
                            vertices.add(x+1,y,z+1,1,0,0,r,g,b,1f)
                            vertIndex[3,p4,c] = nv++
                        }
                        indices.add(vertIndex[3,p1,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p3,c])
                        indices.add(vertIndex[3,p2,c])
                        indices.add(vertIndex[3,p4,c])
                        indices.add(vertIndex[3,p3,c])
                    }
                    if(fronts[size[x,y,z]]!=b0) {
                        val c = fronts[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z+1]
                        val p2 = s1[x+1,y+1,z+1]
                        val p3 = s1[x,y,z+1]
                        val p4 = s1[x+1,y,z+1]
                        if(vertIndex[4,p1,c]==-1){
                            vertices.add(x,y+1,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p1,c] = nv++
                        }
                        if(vertIndex[4,p2,c]==-1){
                            vertices.add(x+1,y+1,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p2,c] = nv++
                        }
                        if(vertIndex[4,p3,c]==-1){
                            vertices.add(x,y,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p3,c] = nv++
                        }
                        if(vertIndex[4,p4,c]==-1){
                            vertices.add(x+1,y,z+1,0,0,1,r,g,b,1f)
                            vertIndex[4,p4,c] = nv++
                        }
                        indices.add(vertIndex[4,p1,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p3,c])
                        indices.add(vertIndex[4,p2,c])
                        indices.add(vertIndex[4,p4,c])
                        indices.add(vertIndex[4,p3,c])
                    }
                    if(backs[size[x,y,z]]!=b0) {
                        val c = backs[size[x,y,z]].toInt() and 0xFF
                        val (r,g,b) = rgbf(c)
                        val p1 = s1[x,y+1,z]
                        val p2 = s1[x+1,y+1,z]
                        val p3 = s1[x,y,z]
                        val p4 = s1[x+1,y,z]
                        if(vertIndex[5,p1,c]==-1){
                            vertices.add(x,y+1,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p1,c] = nv++
                        }
                        if(vertIndex[5,p2,c]==-1){
                            vertices.add(x+1,y+1,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p2,c] = nv++
                        }
                        if(vertIndex[5,p3,c]==-1){
                            vertices.add(x,y,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p3,c] = nv++
                        }
                        if(vertIndex[5,p4,c]==-1){
                            vertices.add(x+1,y,z,0,0,-1,r,g,b,1f)
                            vertIndex[5,p4,c] = nv++
                        }
                        indices.add(vertIndex[5,p1,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p3,c])
                        indices.add(vertIndex[5,p2,c])
                        indices.add(vertIndex[5,p4,c])
                        indices.add(vertIndex[5,p3,c])
                    }
                }
            }
        }
        println("顶点数:${vertices.size},索引数:${indices.size},点数:${vertices.size/10},面数:${indices.size/3}")
        return vertices.toFloatArray() to indices.toIntArray()
    }
}
fun MutableList<Float>.add(x: Int,y: Int,z: Int,nx: Int,ny: Int,nz: Int,r: Float,g: Float,b: Float,a: Float): MutableList<Float> {
    this.add(x,y,z,nx,ny,nz).add(r,g,b,a)
    return this
}
fun MutableList<Float>.add(vararg i: Int): MutableList<Float> {
    i.forEach { add(it.toFloat()) }
    return this
}
fun MutableList<Float>.add(vararg i: Float): MutableList<Float> {
    i.forEach { add(it) }
    return this
}
operator fun MutableMap<String, Int>.get(i: Int,j: Int,k: Int): Int {
    return this["$i-$j-$k"]?:-1
}

operator fun MutableMap<String, Int>.set(i: Int,j: Int,k: Int, v: Int) {
    this["$i-$j-$k"] = v
}