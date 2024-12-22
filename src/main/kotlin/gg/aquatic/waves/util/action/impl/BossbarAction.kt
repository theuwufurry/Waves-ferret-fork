package gg.aquatic.waves.util.action.impl

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBossBar
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.bossbar.AquaticBossBar
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import java.util.*

class BossbarAction : AbstractAction<Player>() {

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val message = (args["message"] as String).updatePAPIPlaceholders(binder)
        val progress = args["progress"].toString().toFloat()
        val color = BossBar.Color.valueOf((args["color"] as String).uppercase())
        val style = BossBar.Overlay.valueOf((args["style"] as String).uppercase())


        val bossBar =
            AquaticBossBar(textUpdater(binder, message).toMMComponent(), color, style, mutableSetOf(), progress)

        val duration = args["duration"] as Int

        WrapperPlayServerBossBar(UUID.randomUUID(), WrapperPlayServerBossBar.Action.ADD).color

        bossBar.addViewer(binder)
        runLaterSync(duration.toLong()) {
            bossBar.removeViewer(binder)
        }
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("message", "", true),
        PrimitiveObjectArgument("progress", 0.0, false),
        PrimitiveObjectArgument("color", "BLUE", false),
        PrimitiveObjectArgument("style", "SOLID", false),
        PrimitiveObjectArgument("duration", 60, true)
    )
}