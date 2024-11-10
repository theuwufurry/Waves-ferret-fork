package gg.aquatic.waves.fake

import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationObject
import gg.aquatic.waves.fake.block.FakeEntity
import gg.aquatic.waves.fake.entity.FakeBlock
import java.util.concurrent.ConcurrentHashMap

class FakeObjectLocationBundle: LocationObject {

    val blocks = ConcurrentHashMap.newKeySet<FakeBlock>()
    val entities = ConcurrentHashMap.newKeySet<FakeEntity>()

}