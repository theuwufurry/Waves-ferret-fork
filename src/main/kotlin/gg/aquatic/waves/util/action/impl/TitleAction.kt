package gg.aquatic.waves.util.action.impl

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player

class TitleAction : AbstractAction<Player>() {

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val title = (args["title"] as String).updatePAPIPlaceholders(binder)
        val subtitle = (args["subtitle"] as String).updatePAPIPlaceholders(binder)
        val fadeIn = args["fade-in"] as Int
        val stay = args["stay"] as Int
        val fadeOut = args["fade-out"] as Int

        val packets = listOf(
            WrapperPlayServerSetTitleText(title.toMMComponent()),
            WrapperPlayServerSetTitleTimes(fadeIn, stay, fadeOut),
            WrapperPlayServerSetTitleSubtitle(subtitle.toMMComponent())
        )

        binder.toUser().let {
            packets.forEach { packet -> it.sendPacket(packet) }
        }
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "", true),
        PrimitiveObjectArgument("subtitle", "", true),
        PrimitiveObjectArgument("fade-in", 0, true),
        PrimitiveObjectArgument("stay", 60, true),
        PrimitiveObjectArgument("fade-out", 0, true)
    )
}