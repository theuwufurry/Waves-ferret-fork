package gg.aquatic.waves.util

import org.bukkit.Location

fun Location.blockLocation(): Location {
    return this.block.location
}