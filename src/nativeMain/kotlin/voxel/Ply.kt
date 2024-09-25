package voxel

import kotlin.math.roundToInt


data class Vertex(
    val x: Float,
    val y: Float,
    val z: Float,
    val nx: Float,
    val ny: Float,
    val nz: Float,
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
)
fun buildVertices(vertices: FloatArray): MutableList<Vertex> {
    val ret = ArrayList<Vertex>(vertices.size / 10)
    for(i in vertices.indices step 10) {
        ret.add(Vertex(
            vertices[i],vertices[i+1],vertices[i+2],
            vertices[i+3],vertices[i+4],vertices[i+5],
            vertices[i+6],vertices[i+7],vertices[i+8],vertices[i+9]
        ))
    }
    return ret
}
fun toVertices(verts: List<Vertex>): FloatArray {
    val arr = FloatArray(verts.size * 10)
    for(i in verts.indices) {
        arr[10*i] = verts[i].x
        arr[10*i+1] = verts[i].y
        arr[10*i+2] = verts[i].z
        arr[10*i+3] = verts[i].nx
        arr[10*i+4] = verts[i].ny
        arr[10*i+5] = verts[i].nz
        arr[10*i+6] = verts[i].r
        arr[10*i+7] = verts[i].g
        arr[10*i+8] = verts[i].b
        arr[10*i+9] = verts[i].a
    }
    return arr
}
data class Quad(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int
) {
    fun canMerge(oth: Quad): Int {
        return if (a == oth.c && b == oth.d) 1
        else if (b == oth.a && d == oth.c) 2
        else if (c == oth.a && d == oth.b) 3
        else if (a == oth.b && c == oth.d) 4
        else if (d == oth.b && c == oth.a) 5
        else 0
    }
    fun merge(oth: Quad, method: Int): Quad {
        return when(method) {
            1 -> Quad(oth.a, oth.b, c, d)
            2 -> Quad(a, oth.b, c, oth.d)
            3 -> Quad(a, b, oth.c, oth.d)
//            4 -> Quad(oth.a, b, c, oth.d)
            4 -> Quad(oth.a, b, oth.c, d)
            5 -> Quad(a, b, oth.c, oth.d)
//            5 -> Quad(oth.a, d, oth.c, d)
//            6 -> Quad(a, oth.d, c, oth.b)
//            7 -> Quad(oth.a, d, oth.c ,b)
//            8 -> Quad(a, oth.d, c, oth.b)
            else -> this
        }
    }
    val graph: String
        get() = """
            $a--$b
            | / |
            $c--$d
        """.trimIndent()
}
fun buildQuad(indices: IntArray, offset: Int): Quad {
    return Quad(indices[offset], indices[offset+1], indices[offset+2], indices[offset+4])
}
fun buildQuads(indices: IntArray): MutableList<Quad> {
    val quads = ArrayList<Quad>(indices.size / 6)
    for(i in indices.indices step 6) {
        quads.add(buildQuad(indices, i))
    }
    return quads
}
fun compressVertices(vertices: FloatArray, indices: IntArray): FloatArray {
    val verts = buildVertices(vertices)
    val min = indices.min()
    var max = indices.max()
//    println("vertices size: ${verts.size}, index max: $max")
    var i = min + 1
    while(i < max) {
        if(!indices.contains(i)){
//            println("vertices size: ${verts.size}, remove at: $i")
            verts.removeAt(i)
            for(a in indices.indices) {
                if(indices[a]>i) indices[a] = indices[a]-1
            }
            max--
        }else {
            i++
        }
    }
    return toVertices(verts)
}
fun merge(quads: MutableList<Quad>) {
    var merged = true
    while(merged) {
        merged = false
        var i = 0
        while(i<quads.size) {
            var qa = quads[i]
            var j = i + 1
            while(j<quads.size){
                val qb = quads[j]
                if(qa.canMerge(qb)>0){
                    merged = true
                    quads[i] = qa.merge(qb, qa.canMerge(qb))
                    quads.removeAt(j)
                    qa = quads[i]
                }else{
                    j++
                }
            }
            i++
        }
    }
}
fun toIndices(quads: List<Quad>): IntArray {
    val arr = IntArray(quads.size * 6)
    var i = 0
    quads.forEach { quad->
        arr[i] = quad.a
        arr[i+1] = quad.c
        arr[i+2] = quad.b

        arr[i+3] = quad.b
        arr[i+4] = quad.c
        arr[i+5] = quad.d
        i+=6
    }
    return arr
}

fun convertPly(_v: FloatArray, _i: IntArray, binary: Boolean = true): ByteArray{
    val quads = buildQuads(_i)
    merge(quads)
    val indices = toIndices(quads)
    val vertices = compressVertices(_v, indices)
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
                .append((vertices[p + 6]*255f).roundToInt()).append(' ')
                .append((vertices[p + 7]*255f).roundToInt()).append(' ')
                .append((vertices[p + 8]*255f).roundToInt()).append(' ')
                .append((vertices[p + 9]*255f).roundToInt()).appendLine()
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

            buff[index] = (vertices[p+6]*255f).roundToInt().toByte()
            index ++
            buff[index] = (vertices[p+7]*255f).roundToInt().toByte()
            index ++
            buff[index] = (vertices[p+8]*255f).roundToInt().toByte()
            index ++
            buff[index] = (vertices[p+9]*255f).roundToInt().toByte()
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