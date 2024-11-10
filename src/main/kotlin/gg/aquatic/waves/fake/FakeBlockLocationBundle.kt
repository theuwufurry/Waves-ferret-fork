package gg.aquatic.waves.fake

import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationObject
import io.ktor.util.collections.*
import org.bukkit.entity.Player

class FakeBlockLocationBundle: LocationObject {

    val blocks = ConcurrentSet<FakeBlock>()

}