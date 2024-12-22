package gg.aquatic.waves.util.requirement

import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.generic.ExecutableObject

class ConfiguredRequirement<A>(executableObject: ExecutableObject<A,Boolean>, arguments: Map<String, Any?>) : ConfiguredExecutableObject<A, Boolean>(
    executableObject, arguments
) {

    override fun execute(binder: A, textUpdater: (A, String) -> String): Boolean {
        var negate = false
        if (arguments.containsKey("negate")) {
            negate = arguments["negate"] as Boolean
        }
        val value = executableObject.execute(binder, arguments, textUpdater)
        if (negate) {
            return !value
        }
        return value
    }

}