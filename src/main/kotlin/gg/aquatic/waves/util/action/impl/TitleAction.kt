package gg.aquatic.waves.util.action.impl

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player

class TitleAction : Action<Player> {

    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String) {
        val title = (args.string("title") { str -> textUpdater(binder, str)} ?: return).updatePAPIPlaceholders(binder)
        val subtitle = (args.string("subtitle") { str -> textUpdater(binder, str)} ?: return).updatePAPIPlaceholders(binder)
        val fadeIn = args.int("fade-in") { str -> textUpdater(binder, str)} ?: 0
        val stay = args.int("stay") { str -> textUpdater(binder, str)} ?: 60
        val fadeOut = args.int("fade-out") { str -> textUpdater(binder, str)} ?: 0

        val packets = listOf(
            WrapperPlayServerSetTitleText(title.toMMComponent()),
            WrapperPlayServerSetTitleTimes(fadeIn, stay, fadeOut),
            WrapperPlayServerSetTitleSubtitle(subtitle.toMMComponent())
        )

        binder.toUser()?.let {
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