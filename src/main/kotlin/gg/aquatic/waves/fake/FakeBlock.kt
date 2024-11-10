package gg.aquatic.waves.fake

import gg.aquatic.aquaticseries.lib.block.AquaticBlock
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationCacheHandler
import gg.aquatic.waves.util.blockLocation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

open class FakeBlock(block: AquaticBlock, location: Location,
                override val viewRange: Int, val onInteract: (PlayerInteractEvent) -> Unit = {}
) :
    FakeObject() {
    override val location: Location = location.blockLocation()

    override fun destroy() {
        for (player in isViewing) {
            show(player)
        }
        unregister()
    }

    var block = block
        private set


    init {
        FakeObjectHandler.tickableObjects += this
    }
    fun register() {
        if (registered) return
        registered = true
        var bundle = LocationCacheHandler.getObject(location,FakeObjectLocationBundle::class.java) as? FakeObjectLocationBundle
        if (bundle == null) {
            bundle = FakeObjectLocationBundle()
            LocationCacheHandler.registerObject(bundle, FakeObjectLocationBundle::class.java, location)
        }
        bundle.blocks += this
    }
    fun unregister() {
        if (!registered) return
        registered = false
        val bundle = LocationCacheHandler.getObject(location,FakeObjectLocationBundle::class.java) as? FakeObjectLocationBundle ?: return
        bundle.blocks -= this

        if (bundle.blocks.isEmpty() && bundle.entities.isEmpty()) {
            LocationCacheHandler.unregisterObject(FakeObjectLocationBundle::class.java, location)
        }
    }

    fun changeBlock(aquaticBlock: AquaticBlock) {
        block = aquaticBlock
        for (player in isViewing) {
            show(player)
        }
    }

    override fun addViewer(player: Player) {
        if (viewers.contains(player)) return
        viewers.add(player)
        if (player.location.distanceSquared(location) <= viewRange * viewRange) {
            show(player)
        }
    }

    override fun removeViewer(uuid: UUID) {
        viewers.removeIf { it.uniqueId == uuid }
    }

    override fun removeViewer(player: Player) {
        hide(player)
    }

    override fun show(player: Player) {
        isViewing.add(player)
        player.sendBlockChange(location, block.blockData)
    }

    override fun hide(player: Player) {
        isViewing.remove(player)
        player.sendBlockChange(location, location.block.blockData)
    }

    override fun tick() {
        tickRange()
    }
}