package gg.aquatic.waves.util

import org.bukkit.block.BlockFace

fun Double.decimals(decimalPlaces: Int = 0): String {
    return if (decimalPlaces == 0) {
        this.toInt().toString()
    } else {
        "%.${decimalPlaces}f".format(this)
    }
}

fun Float.decimals(decimalPlaces: Int = 0): String {
    return if (decimalPlaces == 0) {
        this.toInt().toString()
    } else {
        "%.${decimalPlaces}f".format(this)
    }
}

fun Float.toBlockCardinal(): BlockFace {
    var rotation: Float = (this - 180) % 360
    if (rotation < 0) {
        rotation += 360.0f
    }

    return when {
        rotation >= 315 || rotation < 45 -> BlockFace.NORTH
        rotation >= 45 && rotation < 135 -> BlockFace.EAST
        rotation >= 135 && rotation < 225 -> BlockFace.SOUTH
        else -> BlockFace.WEST
    }
}