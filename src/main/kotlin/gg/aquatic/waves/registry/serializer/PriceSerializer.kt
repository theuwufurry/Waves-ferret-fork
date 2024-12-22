package gg.aquatic.waves.registry.serializer

import gg.aquatic.waves.util.price.ConfiguredPrice
import gg.aquatic.waves.util.argument.ArgumentSerializer
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.getPrice
import org.bukkit.configuration.ConfigurationSection

object PriceSerializer {

    inline fun <reified T: Any> fromSection(section: ConfigurationSection): ConfiguredPrice<T>? {
        val type = section.getString("type") ?: return null
        val price = WavesRegistry.getPrice<T>(type)
        if (price == null) {
            println("[AquaticSeriesLib] Price type $type does not exist!")
            return null
        }

        val arguments = price.arguments()
        val args = ArgumentSerializer.load(section, arguments)

        val configuredPrice = ConfiguredPrice(price, args)

        return configuredPrice
    }

    inline fun <reified T: Any> fromSections(sections: List<ConfigurationSection>): List<ConfiguredPrice<T>> {
        return sections.mapNotNull { fromSection(it) }
    }

}