//package gg.aquatic.waves.interactable.settings
//
//import gg.aquatic.waves.util.audience.AquaticAudience
//import gg.aquatic.waves.util.block.AquaticBlock
//import gg.aquatic.waves.fake.block.FakeBlock
//import gg.aquatic.waves.interactable.Interactable
//import gg.aquatic.waves.interactable.InteractableInteractEvent
//import gg.aquatic.waves.interactable.type.BlockInteractable
////import gg.aquatic.waves.util.block.AquaticBlockSerializer
//import gg.aquatic.waves.util.blockLocation
//import org.bukkit.Location
//import org.bukkit.configuration.ConfigurationSection
//import org.bukkit.util.Vector
//
//class BlockInteractableSettings(
//    val block: AquaticBlock,
//    val offset: Vector
//) : InteractableSettings {
//    override fun build(
//        location: Location,
//        audience: AquaticAudience,
//        onInteract: (InteractableInteractEvent) -> Unit
//    ): Interactable {
//        val fb = FakeBlock(block, location.clone().add(offset).blockLocation(), 50, audience)
//        fb.register()
//        return BlockInteractable(fb, onInteract)
//    }
//
//    companion object: InteractableSettingsFactory {
//        override fun load(section: ConfigurationSection): InteractableSettings {
//            val block = AquaticBlockSerializer.load(section)
//            val offsetStrs = section.getString("offset", "0;0;0")!!.split(";")
//            val offset = Vector(
//                offsetStrs.getOrElse(0) { "0" }.toDouble(),
//                offsetStrs.getOrElse(1) { "0" }.toDouble(),
//                offsetStrs.getOrElse(2) { "0" }.toDouble()
//            )
//            return BlockInteractableSettings(block, offset)
//        }
//
//    }
//}