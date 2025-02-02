package gg.aquatic.waves.util.requirement

import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.generic.ExecutableObject

class ConfiguredRequirement<A>(executableObject: ExecutableObject<A,Boolean>, arguments: ObjectArguments) : ConfiguredExecutableObject<A, Boolean>(
    executableObject, arguments
) {

    override fun execute(binder: A, textUpdater: (A, String) -> String): Boolean {
        val negate = arguments.boolean("negate") { str -> textUpdater(binder, str) } ?: false
        val value = executableObject.execute(binder, arguments, textUpdater)
        if (negate) {
            return !value
        }
        return value
    }

}