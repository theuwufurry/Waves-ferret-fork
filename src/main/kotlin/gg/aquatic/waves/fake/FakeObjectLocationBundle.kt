package gg.aquatic.waves.fake

import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationObject
import gg.aquatic.waves.fake.block.FakeEntity
import gg.aquatic.waves.fake.entity.FakeBlock
import io.ktor.util.collections.*

class FakeObjectLocationBundle: LocationObject {

    val blocks = ConcurrentSet<FakeBlock>()
    val entities = ConcurrentSet<FakeEntity>()

}