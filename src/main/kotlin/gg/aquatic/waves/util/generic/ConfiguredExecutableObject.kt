package gg.aquatic.waves.util.generic

import gg.aquatic.waves.util.argument.ObjectArguments

open class ConfiguredExecutableObject<B, C>(
    val executableObject: ExecutableObject<B,C>,
    val arguments: ObjectArguments
) {

    open fun execute(binder: B, textUpdater: (B, String) -> String): C {
        return executableObject.execute(binder, arguments, textUpdater)
    }

}