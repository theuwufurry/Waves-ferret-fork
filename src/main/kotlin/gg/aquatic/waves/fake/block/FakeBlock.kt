package gg.aquatic.waves.fake.block

import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
import gg.aquatic.waves.chunk.cache.ChunkCacheHandler
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedChunks
import gg.aquatic.waves.fake.FakeObject
import gg.aquatic.waves.fake.FakeObjectChunkBundle
import gg.aquatic.waves.fake.FakeObjectHandler
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.blockLocation
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
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
        destroyed = true
        for (player in isViewing) {
            hide(player)
        }
        FakeObjectHandler.tickableObjects -= this
        unregister()
        FakeObjectHandler.locationToBlocks[location.blockLocation()]?.remove(this)
    }

    var block = block
        private set


    init {
        this.audience = audience
        FakeObjectHandler.locationToBlocks.getOrPut(location.blockLocation()) { ConcurrentHashMap.newKeySet() } += this
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
        if (isViewing.contains(player)) {
            hide(player)
        }
        viewers.remove(player)
    }

    override fun show(player: Player) {
        isViewing.add(player)
        /*
        val packet = WrapperPlayServerBlockChange(
            Vector3i(location.blockX,location.blockY,location.blockZ),
            SpigotConversionUtil.fromBukkitBlockData(block.blockData).globalId
        )
        player.toUser().sendPacket(packet)
         */
        player.sendBlockChange(location, block.blockData)
    }

    override fun hide(player: Player) {
        isViewing.remove(player)
        val packet = WrapperPlayServerBlockChange(
            Vector3i(location.blockX, location.blockY, location.blockZ),
            SpigotConversionUtil.fromBukkitBlockData(location.block.blockData).globalId
        )
        player.toUser()?.sendPacket(packet)
    }

    override fun tick() {

    }
}