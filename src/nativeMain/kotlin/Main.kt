import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import voxel.VoxelLoader
import voxel.convertPly

@Serializable
data class Mesh (
    val vertices: FloatArray,
    val indices: IntArray
)

// -i input -o output [-t type:txt|json|ply]
fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println(
            """
            Usage: vox infile [outfile] [outtype: txt|json|ply(plya,plyb)]
        """.trimIndent()
        )
    } else {
        var infile = ""
        var outfile = ""
        // plaintext:0, json:1, ply/ascii:2, ply/binary:3
        var type = 3
        if (args.size == 1) {
            infile = args[0]
            val tmp = infile.split('.').last()
            outfile = infile.substring(0, infile.length - tmp.length) + "ply"
        }
        if (args.size >= 2) {
            outfile = args[1]
            infile = args[0]
        }
        if (args.size == 3) {
            type = when (args[2]) {
                "json" -> 1
                "ply", "plya" -> 2
                "txt" -> 0
                else -> 3
            }
        }
        val fi = infile.toPath()
        val fo = outfile.toPath()
        runCatching {
            FileSystem.SYSTEM.read(fi) {
                val vc = VoxelLoader.load(readByteArray())
                if (FileSystem.SYSTEM.exists(fo)) {
                    FileSystem.SYSTEM.delete(fo)
                }
                val sink = FileSystem.SYSTEM.appendingSink(fo, false)
                sink.buffer().let { io ->
                    vc.forEach { c ->
                        if (type == 0) {
                            val (v, i) = c.optimization().buildMesh()
                            io.write(v.joinToString(",").encodeToByteArray())
                            io.write("\r\n".encodeToByteArray())
                            io.write(i.joinToString(",").encodeToByteArray())
                            io.write("\r\n".encodeToByteArray())
                        } else if (type == 1) {
                            val (v, i) = c.optimization().buildMesh()
                            io.write(PrettyPrintJson.encodeToString(Mesh(v, i)).encodeToByteArray())
                        } else if (type == 2) {
                            val (v,i) = c.optimization().buildArray()
                            io.write(convertPly(v,i))
//                            io.write(c.optimization().exportPly())
                        } else if (type == 3) {
                            val (v,i) = c.optimization().buildArray()
                            io.write(convertPly(v,i, true))
//                            io.write(c.optimization().exportPly(true))
                        }
                    }
                    io.flush()
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
}

private val PrettyPrintJson = Json {
    prettyPrint = true
}