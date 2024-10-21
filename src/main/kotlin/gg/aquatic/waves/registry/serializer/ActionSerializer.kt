package gg.aquatic.waves.registry.serializer

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.getAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.configuration.ConfigurationSection

object ActionSerializer {

    suspend inline fun <reified T : Any> fromSection(
        section: ConfigurationSection
    ): ConfiguredAction<T>? = withContext(Dispatchers.IO) {
        val type = section.getString("type") ?: return@withContext null
        val action = WavesRegistry.getAction<T>(type)
        if (action == null) {
            println("[AquaticSeriesLib] Action type $type does not exist!")
            return@withContext null
        }

        val arguments = action.arguments()
        val args = AquaticObjectArgument.loadRequirementArguments(section, arguments)

        val configuredAction = ConfiguredAction(action, args)
        return@withContext configuredAction
    }

    suspend inline fun <reified T : Any> fromSections(sections: List<ConfigurationSection>): List<ConfiguredAction<T>> {
        return sections.mapNotNull { fromSection(it) }
    }

}