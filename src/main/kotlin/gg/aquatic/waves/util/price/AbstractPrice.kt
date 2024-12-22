package gg.aquatic.waves.util.price

import gg.aquatic.waves.util.argument.AquaticObjectArgument

abstract class AbstractPrice<T> {

    abstract fun take(binder: T, arguments: Map<String,Any?>)
    abstract fun give(binder: T, arguments: Map<String,Any?>)
    abstract fun set(binder: T, arguments: Map<String,Any?>)
    abstract fun has(binder: T, arguments: Map<String,Any?>): Boolean

    abstract fun arguments(): List<AquaticObjectArgument<*>>

}