package gg.aquatic.waves.util.generic

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments

interface ExecutableObject<A, B> {

    fun execute(binder: A, args: ObjectArguments, textUpdater: (A, String) -> String): B
    val arguments: List<AquaticObjectArgument<*>>

}