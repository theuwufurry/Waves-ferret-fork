package gg.aquatic.waves.interactable.settings

import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Location

interface InteractableSettings {
    fun build(location: Location, audience: AquaticAudience, onInteract: (InteractableInteractEvent) -> Unit): Interactable
}