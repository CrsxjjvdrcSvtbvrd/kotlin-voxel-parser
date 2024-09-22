# Kotlin/Native Parser for MagicaVoxel

parse .vox file to vertices and indices

convert .vox to .ply

OptVoxel.kt > buildMesh ready for libgdx(should convert intarray to shortarray) or lwjgl

```kotlin
val vc = VoxelLoader.load(fileBuffer)
val (v, i) = c.optimization().buildMesh()
val mesh = Mesh(true,
    v.size,
    i.size,
    VertexAttribute.Position(),
    VertexAttribute.Normal(),
    VertexAttribute.ColorUnpacked())
mesh.setVertices(v.toFloatArray())
mesh.setIndices(i.toShortArray())
```