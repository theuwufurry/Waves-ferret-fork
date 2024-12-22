package gg.aquatic.waves.registry.serializer

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.getAction
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.configuration.ConfigurationSection

object ActionSerializer {

    inline fun <reified T : Any> fromSection(
        section: ConfigurationSection
    ): ConfiguredExecutableObject<T,Unit>? {
        val type = section.getString("type") ?: return null
        val action = WavesRegistry.getAction<T>(type)
        if (action == null) {
            println("[AquaticSeriesLib] Action type $type does not exist!")
            return null
        }

        val args = AquaticObjectArgument.loadRequirementArguments(section, action.arguments)

        val configuredAction = ConfiguredExecutableObject(action, args)
        return configuredAction
    }

    inline fun <reified T : Any> fromSections(sections: List<ConfigurationSection>): List<ConfiguredExecutableObject<T,Unit>> {
        return sections.mapNotNull { fromSection(it) }
    }

}