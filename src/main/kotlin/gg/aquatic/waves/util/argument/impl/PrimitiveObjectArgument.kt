package gg.aquatic.waves.util.argument.impl

import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection

class PrimitiveObjectArgument(id: String, defaultValue: Any?, required: Boolean) : AquaticObjectArgument<Any?>(id, defaultValue,
    required
) {
    override val serializer: AbstractObjectArgumentSerializer<Any?>
        get() {
            return Serializer
        }

    override fun load(section: ConfigurationSection): Any? {
        return serializer.load(section, id) ?: defaultValue
    }

    object Serializer: AbstractObjectArgumentSerializer<Any?>() {
        override fun load(section: ConfigurationSection, id: String): Any? {
            return section.get(id)
        }
    }
}