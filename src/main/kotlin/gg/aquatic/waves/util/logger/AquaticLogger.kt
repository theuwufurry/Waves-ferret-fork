package gg.aquatic.waves.util.logger

import gg.aquatic.waves.Waves
import gg.aquatic.waves.util.logger.type.DebugLogger
import gg.aquatic.waves.util.logger.type.InfoLogger

object AquaticLogger {

    val id: String
        get() {
            return Waves.INSTANCE.name
        }
    var debugEnabled = false
}

fun logInfo(message: String) {
    InfoLogger.send(message)
}

fun logDebug(message: String) {
    DebugLogger.send(message)
}