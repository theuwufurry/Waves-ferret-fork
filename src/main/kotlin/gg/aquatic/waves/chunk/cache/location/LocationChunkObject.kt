package gg.aquatic.waves.chunk.cache.location

import gg.aquatic.waves.chunk.cache.ChunkObject

class LocationChunkObject: ChunkObject {

    val cache = HashMap<String,MutableMap<Class<out LocationObject>, LocationObject>>()

}