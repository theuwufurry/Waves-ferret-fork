package gg.aquatic.waves.util.action.impl

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
        val fadeIn = args["fadeIn"] as Int
        val stay = args["stay"] as Int
        val fadeOut = args["fadeOut"] as Int

        binder.toUser().sendPacket(
            WrapperPlayServerTitle(WrapperPlayServerTitle.TitleAction.SET_TIMES_AND_DISPLAY, title.toMMComponent(), subtitle.toMMComponent(), null, fadeIn, stay, fadeOut)
        )
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "", true),
        PrimitiveObjectArgument("subtitle", "", true),
        PrimitiveObjectArgument("fadeIn", 0, true),
        PrimitiveObjectArgument("stay", 60, true),
        PrimitiveObjectArgument("fadeOut", 0, true)
    )
}