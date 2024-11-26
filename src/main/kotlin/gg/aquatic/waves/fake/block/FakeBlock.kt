package gg.aquatic.waves.fake.block

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.block.AquaticBlock
import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.util.runLaterAsync
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedChunks
import gg.aquatic.waves.fake.FakeObject
import gg.aquatic.waves.fake.FakeObjectHandler
import gg.aquatic.waves.fake.FakeObjectChunkBundle
import gg.aquatic.waves.util.blockLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class FakeBlock(
    block: AquaticBlock, location: Location,
    override val viewRange: Int,
    audience: AquaticAudience,
    var onInteract: (FakeBlockInteractEvent) -> Unit = {}
) : FakeObject() {
    override val location: Location = location.blockLocation()

    override var audience = audience
        set(value) {
            field = value
            for (viewer in viewers.toMutableList()) {
                if (field.canBeApplied(viewer) && viewer.isOnline) continue
                removeViewer(viewer)
            }
            for (player in
            Bukkit.getOnlinePlayers().filter { !viewers.contains(it) }) {
                if (!field.canBeApplied(player)) continue
                addViewer(player)
            }
        }

    override fun destroy() {
        for (player in isViewing) {
            hide(player)
        }
        FakeObjectHandler.tickableObjects -= this
        unregister()
        FakeObjectHandler.locationToBlocks[location]?.remove(this)
        destroyed = true
    }

    var block = block
        private set


    init {
        this.audience = audience
        val set = if (FakeObjectHandler.locationToBlocks.contains(location)) {
            FakeObjectHandler.locationToBlocks[location]!!
        } else {
            val set = ConcurrentHashMap.newKeySet<FakeBlock>()
            FakeObjectHandler.locationToBlocks[location] = set
            set
        }
        set += this
        FakeObjectHandler.tickableObjects += this

        for (viewer in viewers) {
            if (viewer.trackedChunks().contains(location.chunk.chunkId())) {
                show(viewer)
            }
        }
    }

    fun register() {
        if (registered) return
        registered = true
        var bundle = ChunkCacheHandler.getObject(
            location.chunk,
            FakeObjectChunkBundle::class.java
        ) as? FakeObjectChunkBundle
        if (bundle == null) {
            bundle = FakeObjectChunkBundle()
            ChunkCacheHandler.registerObject(bundle, location.chunk)
        }
        bundle.blocks += this
    }

    fun unregister() {
        if (!registered) return
        registered = false
        val bundle = ChunkCacheHandler.getObject(
            location.chunk,
            FakeObjectChunkBundle::class.java
        ) as? FakeObjectChunkBundle ?: return
        bundle.blocks -= this
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
        if (player.world.name != location.world!!.name) return
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

    }
}