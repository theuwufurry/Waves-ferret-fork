package gg.aquatic.waves.registry.serializer

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.getAction
import org.bukkit.configuration.ConfigurationSection

object ActionSerializer {

    inline fun <reified T : Any> fromSection(
        section: ConfigurationSection
    ): ConfiguredAction<T>? {
        val type = section.getString("type") ?: return null
        val action = WavesRegistry.getAction<T>(type)
        if (action == null) {
            println("[AquaticSeriesLib] Action type $type does not exist!")
            return null
        }

        val arguments = action.arguments()
        val args = AquaticObjectArgument.loadRequirementArguments(section, arguments)

        val configuredAction = ConfiguredAction(action, args)
        return configuredAction
    }

    inline fun <reified T : Any> fromSections(sections: List<ConfigurationSection>): List<ConfiguredAction<T>> {
        return sections.mapNotNull { fromSection(it) }
    }

}