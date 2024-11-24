package gg.aquatic.waves.util

import gg.aquatic.aquaticseries.lib.chance.IChance
import gg.aquatic.aquaticseries.lib.util.decimals

fun Collection<IChance>.realChance(item: IChance): Double {
    var total = 0.0
    for (chance in this) {
        total += chance.chance
    }
    return if (total > 0) item.chance / total else 0.0
}

fun Collection<IChance>.realChanceFormatted(item: IChance): Double {
    val chance = realChance(item)
    return (chance*100.0).decimals(2).toDouble()
}

fun <T: IChance> Collection<T>.realChance(item: T): HashMap<T, Double> {
    var total = 0.0
    for (chance in this) {
        total += chance.chance
    }
    val realChance = HashMap<T, Double>()
    for (chance in this) {
        realChance[chance] = chance.chance / total
    }
    return realChance
}

fun <T: IChance> Collection<T>.realChanceFormatted(item: T): HashMap<T, Double> {
    var total = 0.0
    for (chance in this) {
        total += chance.chance
    }
    val realChance = HashMap<T, Double>()
    for (chance in this) {
        realChance[chance] = ((chance.chance / total)*100.0).decimals(2).toDouble()
    }
    return realChance
}