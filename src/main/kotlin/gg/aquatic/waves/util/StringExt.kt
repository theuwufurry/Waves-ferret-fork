package gg.aquatic.waves.util

import gg.aquatic.aquaticseries.lib.adapt.AquaticString
import gg.aquatic.aquaticseries.paper.adapt.PaperString
import gg.aquatic.aquaticseries.spigot.adapt.SpigotString
import net.kyori.adventure.text.Component

fun AquaticString.toComponent(): Component {
    return if (this is SpigotString) {
        Component.text(this.string)
    } else {
        (this as PaperString).convert()
    }
}

fun List<AquaticString>.toComponent(): List<Component> = this.map { it.toComponent() }