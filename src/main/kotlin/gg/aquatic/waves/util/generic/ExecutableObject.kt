package gg.aquatic.waves.util.generic

import gg.aquatic.waves.util.argument.AquaticObjectArgument

interface ExecutableObject<A, B> {

    fun execute(binder: A, args: Map<String,Any?>, textUpdater: (A, String) -> String): B
    val arguments: List<AquaticObjectArgument<*>>

}