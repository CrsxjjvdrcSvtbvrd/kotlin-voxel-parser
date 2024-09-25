package voxel
/*
import okio.FileSystem
import okio.Path.Companion.toPath

data class Line(
    val a: Int,
    val b: Int
)
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
//        else if (a == oth.d && b == oth.c) 1
        else if (b == oth.a && d == oth.c) 2
//        else if (b == oth.c && d == oth.a) 2
        else if (c == oth.a && d == oth.b) 3
//        else if (c == oth.b && d == oth.a) 3
        else if (a == oth.b && c == oth.d) 4
//        else if (a == oth.d && c == oth.b) 4
        else 0
    }
    fun merge(oth: Quad, method: Int): Quad {
        return when(method) {
            1 -> Quad(oth.a, oth.b, c, d)
            2 -> Quad(a, oth.b, c, oth.d)
            3 -> Quad(a, b, oth.c, oth.d)
            4 -> Quad(oth.a, b, c, oth.d)
            else -> this
        }
    }
    fun merge(oth: Quad): Pair<Boolean, Quad> {
        val cm = canMerge(oth)
        if(cm==0) return false to this
        else return true to merge(oth, cm)
    }
//    fun appendIndices(list: MutableList<Int>) {
//        list.add(a)
//        list.add(b)
//        list.add(c)
//        list.add(a)
//        list.add(d)
//        list.add(c)
//    }
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
fun mergeQuads(quads: MutableList<Quad>): MutableList<Quad> {
    var merged = true
    while(merged) {
        merged = false
        val newQuads = mutableListOf<Quad>()
        var mergedQuad: Quad? = null
        var i = 0
        while(i < quads.size) {
            val quadA = quads[i]
            for(j in i+1 until quads.size) {
                val quadB = quads[j]
                if(quadA.canMerge(quadB)>0) {
                    mergedQuad = quadA.merge(quadB, quadA.canMerge(quadB))
//                    println("""
//                        ${quadA.a}-${quadA.b}      ${quadB.a}-${quadB.b}    ==\   ${mergedQuad.a}-${mergedQuad.b}
//                        ${quadA.c}-${quadA.d}      ${quadB.c}-${quadB.d}    ==/   ${mergedQuad.c}-${mergedQuad.d}
//                    """.trimIndent())
                    merged = true
                    quads.removeAt(j)
                    break
                }
            }
            if(mergedQuad!=null){
                newQuads.add(mergedQuad)
                println("add")
                println(mergedQuad.graph)
            }else{
                newQuads.add(quadA)
                println("add")
                println(quadA.graph)
            }
            i++
        }
        quads.clear()
        quads.addAll(newQuads)
        println("clear")
    }
    return quads
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
fun doCompress() {
//    val chunk = VoxelChunk(VoxelLoader.DEFAULT_PALETTE, Int3(3,3,3))
//    chunk.data = byteArrayOf(
//        1,1,1,1,
//        1,1,2,1
//    )
//    val (vertices, indices) = chunk.optimization().buildArray()
//    val quads0 = buildQuads(indices)
//    println("src")
//    quads0.forEach {
//        println(it.graph)
//    }
//    println("merge")
    // ===
//    merge(quads0)
    // ===
//    quads0.forEach {
//        println(it.graph)
//    }

//    val quads = mergeQuads(quads0)
//    quads.forEach {
//        println(it.graph)
//    }

    // ===
//    val arr = toIndices(quads0)
//    FileSystem.SYSTEM.write("test/output0.ply".toPath()) {
//        write(convertPly(vertices, indices, false))
//    }
//    FileSystem.SYSTEM.write("test/output1.ply".toPath()) {
//        write(convertPly(vertices, arr, false))
//    }
//    FileSystem.SYSTEM.write("test/output2.ply".toPath()) {
//        val nrr = compressVertices(vertices, arr)
//        write(convertPly(nrr, arr, false))
//    }
    // ===

    FileSystem.SYSTEM.read("test/obj_trellis.vox".toPath()){
        val chunk = VoxelLoader.load(readByteArray())[0]
        val (vertices, indices) = chunk.optimization().buildArray()
        println("indices: ${indices.size}")
        val quads = buildQuads(indices)
        merge(quads)
        val arr = toIndices(quads)
        FileSystem.SYSTEM.write("test/output0.ply".toPath()) {
            write(convertPly(vertices, indices, false))
        }
        FileSystem.SYSTEM.write("test/output1.ply".toPath()) {
            write(convertPly(vertices, arr, false))
        }
        FileSystem.SYSTEM.write("test/output2.ply".toPath()) {
            val nrr = compressVertices(vertices, arr)
            write(convertPly(nrr, arr, false))
        }
    }
}
fun compressTest() {
    doCompress()
    return
    val chunk = VoxelChunk(VoxelLoader.DEFAULT_PALETTE, Int3(3,3,3))
    chunk.data = byteArrayOf(
        1,1,1,1,
        1,1,2,1
    )
    val (vertices, indices) = chunk.optimization().buildArray()
    val quads = buildQuads(indices)
    quads.forEach {
        println(it.graph)
    }
    println("==========")
    mergeQuads(quads).forEach {
        println(it.graph)
    }
//    val indices = intArrayOf(
//        1,2,3,2,4,3,
//        1,5,2,5,6,2,
//        5,9,6,9,10,6,
//        10,9,12,9,11,12,
//        2,6,4,6,8,4,
//        6,10,8,10,12,8,
//        4,7,3,4,8,7,
//        8,11,7,8,12,11,
//        1,3,5,5,3,7,
//        5,7,9,9,7,11
//    )
//    val quads = buildQuads(indices)
//    mergeQuads(quads).forEach {
//        println(it.graph)
//    }
//    println(indices.joinToString(","))
//    val quads = buildQuads(indices)
//    println(quads.size)
//    depthCompress(quads).forEach {
//        println(it.graph)
//    }
//    compressQuads(quads).forEach {
//        println(it.graph)
//    }
//    println("====")
//    compressQuads(compressQuads(quads)).forEach {
//        println(it.graph)
//    }
//    quads.forEach {
//        println(it.graph)
//    }
//    println("=====")
//    mergeQuads(quads).forEach {
//        println(it.graph)
//    }
//    println(quads[0].canMerge(quads[1]))
//    println(quads[0].canMerge(quads[3]))
//    println(quads[0].graph)
//    println(quads[1].graph)
//    println(quads[0].merge(quads[1],quads[0].canMerge(quads[1])).graph)
}

//package voxel
//
//data class Edge(
//    val v1: Int,
//    val v2: Int
//)
//data class Face(
//    val v1: Int,
//    val v2: Int,
//    val v3: Int
//)
//fun<K,V> MutableMap<K,V>.computeIfAbsent(key: K, block: ()->V): V {
//    if(this[key]==null) {
//        val v = block()
//        this[key] = v
//        return v
//    }
//    return this[key]!!
//}
//fun buildFaces(indices: IntArray): List<Face> {
//    val faces = ArrayList<Face>(indices.size / 3)
//    for(i in indices.indices step 3) {
//        faces.add(Face(indices[i], indices[i+1], indices[i+2]))
//    }
//    return faces
//}
//fun findSharedEdges(faces: List<Face>): Map<Edge, List<Face>> {
//    val edgeMap = mutableMapOf<Edge, MutableList<Face>>()
//    fun addEdge(v1: Int, v2: Int, face: Face) {
//        val edge = Edge(minOf(v1, v2), maxOf(v1, v2))
//        edgeMap.computeIfAbsent(edge) { mutableListOf() }.add(face)
//    }
//    for(face in faces) {
//        addEdge(face.v1, face.v2, face)
//        addEdge(face.v2, face.v3, face)
//        addEdge(face.v1, face.v3, face)
//    }
//    return edgeMap.filter { it.value.size>1 }
//}
//fun mergeFaces(faces: List<Face>, sharedEdges: Map<Edge, List<Face>>): List<Face> {
//    val optimizedFaces = mutableListOf<Face>()
//    val merged = mutableSetOf<Face>()
//    for((edge, sharedFaces) in sharedEdges) {
//        if(sharedFaces.size == 2) {
//            val face1 = sharedFaces[0]
//            val face2 = sharedFaces[1]
//            val vertices = setOf(
//                face1.v1, face1.v2, face1.v3,
//                face2.v1, face2.v2, face2.v3
//            )
//            val mergedFace = Face(vertices.elementAt(0), vertices.elementAt(1), vertices.elementAt(2))
//            optimizedFaces.add(mergedFace)
//            merged.add(face1)
//            merged.add(face2)
//        }
//    }
//    faces.forEach { face->
//        if(!merged.contains(face)) {
//            optimizedFaces.add(face)
//        }
//    }
//    return optimizedFaces
//}
//fun generateOptimizedIndices(faces: List<Face>): IntArray {
//    return faces.flatMap { listOf(it.v1, it.v2, it.v3) }.toIntArray()
//}
//fun optimizeMesh(vertices: FloatArray, indices: IntArray): Pair<FloatArray, IntArray> {
//    val faces = buildFaces(indices)
//    val sharedEdges = findSharedEdges(faces)
//    println("shared edges: ${sharedEdges.size}")
//    val optimizedFaces = mergeFaces(faces, sharedEdges)
//    return vertices to generateOptimizedIndices(optimizedFaces)
//}
//fun optimizeMesh(indices: IntArray): IntArray {
//    val faces = buildFaces(indices)
//    val sharedEdges = findSharedEdges(faces)
//    println("shared edges: ${sharedEdges.size}")
//    val optimizedFaces = mergeFaces(faces, sharedEdges)
//    return generateOptimizedIndices(optimizedFaces)
//}

*/