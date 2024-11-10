package gg.aquatic.waves.fake

import io.ktor.util.collections.*
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

abstract class FakeObject {

    abstract val location: Location
    protected var registered: Boolean = false
    abstract val viewRange: Int

    // List of players that can see the object
    val viewers = ConcurrentSet<Player>()
    // List of players that currently got the chunk loaded
    val loadedChunkViewers = ConcurrentSet<Player>()
    // List of players that are currently viewing the object
    val isViewing = ConcurrentSet<Player>()

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
    protected fun tickRange() {
        rangeTick++
        if (rangeTick % 4 == 0) {
            rangeTick = 0
        } else {
            return
        }

        for (loadedChunkViewer in loadedChunkViewers.toSet()) {
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