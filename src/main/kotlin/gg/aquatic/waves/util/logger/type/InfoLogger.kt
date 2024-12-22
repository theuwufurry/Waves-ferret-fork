package gg.aquatic.waves.util.logger.type

import gg.aquatic.waves.util.logger.AquaticLogger
import gg.aquatic.waves.util.logger.ILogger
import org.bukkit.Bukkit

object InfoLogger : ILogger {
    override fun send(message: String) {
        val prefix = "[${AquaticLogger.id}]"
        Bukkit.getConsoleSender().sendMessage("$prefix $message")
    }
}