//package gg.aquatic.waves.interactable.settings
//
//import gg.aquatic.waves.interactable.Interactable
//import gg.aquatic.waves.interactable.InteractableInteractEvent
//import gg.aquatic.waves.interactable.type.MEGInteractable
//import gg.aquatic.waves.util.audience.AquaticAudience
//import org.bukkit.Location
//import org.bukkit.configuration.ConfigurationSection
//import org.bukkit.util.Vector
//
//class MEGInteractableSettings(
//    val offset: Vector,
//    val modelId: String
//): InteractableSettings {
//    override fun build(
//        location: Location,
//        audience: AquaticAudience,
//        onInteract: (InteractableInteractEvent) -> Unit
//    ): Interactable {
//        return MEGInteractable(location.clone().add(offset), modelId, audience, onInteract)
//    }
//
//    companion object : InteractableSettingsFactory {
//        override fun load(section: ConfigurationSection): InteractableSettings {
//            val offsetStrs = section.getString("offset", "0;0;0")!!.split(";")
//            val offset = Vector(
//                offsetStrs.getOrElse(0) { "0" }.toDouble(),
//                offsetStrs.getOrElse(1) { "0" }.toDouble(),
//                offsetStrs.getOrElse(2) { "0" }.toDouble()
//            )
//            val modelId = section.getString("model") ?: ""
//            return MEGInteractableSettings(offset,modelId)
//        }
//
//    }
//}