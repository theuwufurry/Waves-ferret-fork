package gg.aquatic.waves.util.bossbar

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBossBar
import gg.aquatic.waves.util.toUser
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AquaticBossBar(
    message: Component,
    color: net.kyori.adventure.bossbar.BossBar.Color,
    overlay: net.kyori.adventure.bossbar.BossBar.Overlay,
    flags: MutableSet<net.kyori.adventure.bossbar.BossBar.Flag>,
    progress: Float
) {

    private val uuid: UUID = UUID.randomUUID()
    private val viewers = ConcurrentHashMap.newKeySet<Player>()

    var message: Component = message
        set(value) {
            field = value

            val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_TITLE)
            packet.title = value

            for (viewer in viewers) {
                viewer.toUser().sendPacket(packet)
            }
        }
    var color: net.kyori.adventure.bossbar.BossBar.Color = color
        set(value) {
            field = value

            val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_STYLE)
            packet.color = value
            packet.overlay = overlay

            for (viewer in viewers) {
                viewer.toUser().sendPacket(packet)
            }
        }

    var overlay: net.kyori.adventure.bossbar.BossBar.Overlay = overlay
        set(value) {
            field = value
            val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_STYLE)
            packet.overlay = value
            packet.color = color
            for (viewer in viewers) {
                viewer.toUser().sendPacket(packet)
            }
        }

    private var flags: EnumSet<net.kyori.adventure.bossbar.BossBar.Flag> = EnumSet.noneOf(net.kyori.adventure.bossbar.BossBar.Flag::class.java).apply { addAll(flags) }
    fun flags(): Set<net.kyori.adventure.bossbar.BossBar.Flag> = flags.toSet()
    fun addFlag(flag: net.kyori.adventure.bossbar.BossBar.Flag) {
        flags += flag

        val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_FLAGS)
        packet.flags = flags
        for (viewer in viewers) {
            viewer.toUser().sendPacket(packet)
        }
    }
    fun removeFlag(flag: net.kyori.adventure.bossbar.BossBar.Flag) {
        flags -= flag

        val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_FLAGS)
        packet.flags = flags
        for (viewer in viewers) {
            viewer.toUser().sendPacket(packet)
        }
    }

    fun setFlags(flags: Set<net.kyori.adventure.bossbar.BossBar.Flag>) {
        this.flags = EnumSet.noneOf(net.kyori.adventure.bossbar.BossBar.Flag::class.java).apply { addAll(flags) }

        val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_FLAGS)
        packet.flags = this.flags
        for (viewer in viewers) {
            viewer.toUser().sendPacket(packet)
        }
    }

    var progress: Float = progress
        set(value) {
            field = value
            val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.UPDATE_HEALTH)
            packet.health = value
            for (viewer in viewers) {
                viewer.toUser().sendPacket(packet)
            }
        }

    fun addViewer(player: Player) {
        viewers += player

        val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.ADD)
        packet.uuid = uuid
        packet.title = message
        packet.health = progress
        packet.color = color
        packet.overlay = overlay
        packet.flags = flags
        player.toUser().sendPacket(packet)
    }

    fun removeViewer(player: Player) {
        viewers -= player

        val packet = WrapperPlayServerBossBar(uuid,WrapperPlayServerBossBar.Action.REMOVE)
        packet.uuid = uuid
    }

}