package gg.aquatic.waves.util.argument

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

abstract class AquaticObjectArgument<T>(
    val id: String, val defaultValue: T?, val required: Boolean
) {

    abstract val serializer: AbstractObjectArgumentSerializer<T?>
    abstract fun load(section: ConfigurationSection): T?

    companion object {
        fun loadRequirementArguments(
            section: ConfigurationSection,
            arguments: List<AquaticObjectArgument<*>>
        ): ObjectArguments {
            val args: MutableMap<String, Any?> = java.util.HashMap()

            for (argument in arguments) {
                val loaded = argument.load(section)
                if (loaded == null && argument.required) {
                    Bukkit.getConsoleSender()
                        .sendMessage("§cARGUMENT §4" + argument.id + " §cIS MISSING, PLEASE UPDATE YOUR CONFIGURATION!")
                }
                args += argument.id to loaded
            }
            return ObjectArguments(args)
        }
    }

}