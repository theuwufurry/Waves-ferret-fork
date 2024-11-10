package gg.aquatic.waves.util

fun Class<*>.getConstructorOrNull(vararg parameterTypes: Class<*>) =
    try {
        getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }