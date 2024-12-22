package gg.aquatic.waves.util.logger.type

import gg.aquatic.waves.util.logger.AquaticLogger
import gg.aquatic.waves.util.logger.ILogger
import org.bukkit.Bukkit

object DebugLogger: ILogger {
    override fun send(message: String) {
        if (!AquaticLogger.debugEnabled) return
        val prefix = "[DEBUG] [${AquaticLogger.id}]"
        Bukkit.getConsoleSender().sendMessage("$prefix $message")
    }
}