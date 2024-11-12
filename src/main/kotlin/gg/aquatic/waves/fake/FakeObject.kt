package gg.aquatic.waves.fake

import gg.aquatic.waves.chunk.trackedByPlayers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class FakeObject {

    abstract val location: Location
    protected var registered: Boolean = false
    abstract val viewRange: Int
    var destroyed: Boolean = false

    // List of players that can see the object
    val viewers = ConcurrentHashMap.newKeySet<Player>()

    // List of players that are currently viewing the object
    val isViewing = ConcurrentHashMap.newKeySet<Player>()

    abstract fun destroy()
    abstract fun addViewer(player: Player)
    abstract fun removeViewer(uuid: UUID)
    abstract fun removeViewer(player: Player)
    abstract fun show(player: Player)
    abstract fun hide(player: Player)

    abstract fun tick()

    private var rangeTick = 0
    internal fun handleTick() {
        tick()
        tickRange()
    }

    private fun tickRange() {
        rangeTick++
        if (rangeTick % 4 == 0) {
            rangeTick = 0
        } else {
            return
        }

        val loadedChunkViewers = location.chunk.trackedByPlayers().filter { viewers.contains(it) }
        for (loadedChunkViewer in loadedChunkViewers.toSet()) {
            if (!loadedChunkViewer.isOnline) {
                FakeObjectHandler.handlePlayerRemove(loadedChunkViewer, this, true)
                continue
            }
            if (loadedChunkViewer.world != location.world) {
                FakeObjectHandler.handlePlayerRemove(loadedChunkViewer, this)
                continue
            }
            val distance = loadedChunkViewer.location.distanceSquared(location)
            if (isViewing.contains(loadedChunkViewer)) {
                if (distance > viewRange * viewRange) {
                    hide(loadedChunkViewer)
                    isViewing.remove(loadedChunkViewer)
                }
            } else {
                if (distance <= viewRange * viewRange) {
                    show(loadedChunkViewer)
                    isViewing.add(loadedChunkViewer)
                }
            }
        }
    }
}