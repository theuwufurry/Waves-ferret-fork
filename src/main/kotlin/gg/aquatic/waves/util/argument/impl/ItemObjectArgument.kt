package gg.aquatic.waves.util.argument.impl

import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.item.loadFromYml
import org.bukkit.configuration.ConfigurationSection

class ItemObjectArgument(id: String, defaultValue: AquaticItem?, required: Boolean) : AquaticObjectArgument<AquaticItem>(id, defaultValue,
    required
) {
    override val serializer: AbstractObjectArgumentSerializer<AquaticItem?>
        get() {
            return Serializer
        }

    override fun load(section: ConfigurationSection): AquaticItem? {
        return serializer.load(section, id) ?: defaultValue
    }

    object Serializer : AbstractObjectArgumentSerializer<AquaticItem?>() {
        override fun load(section: ConfigurationSection, id: String): AquaticItem? {
            return AquaticItem.loadFromYml(section.getConfigurationSection(id) ?: return null)
        }
    }
}