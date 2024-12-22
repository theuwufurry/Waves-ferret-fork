package gg.aquatic.waves.util.generic

import gg.aquatic.waves.util.requirement.ConfiguredRequirementWithFailActions

class ConfiguredExecutableObjectsWithConditions<A, B>(
    val executableObjects: Collection<ConfiguredExecutableObjectWithConditions<A, B>>,
    val conditions: Collection<ConfiguredRequirementWithFailActions<A,B>>,
    val failExecutableObjects: ConfiguredExecutableObjectsWithConditions<A, B>?
) {

    fun tryExecute(binder: A, textUpdater: (A, String) -> String) {
        for (condition in conditions) {
            if (!condition.tryExecute(binder, textUpdater)) {
                failExecutableObjects?.tryExecute(binder, textUpdater)
                return
            }
        }

        for (executableObject in executableObjects) {
            executableObject.tryExecute(binder, textUpdater)
        }
    }
}