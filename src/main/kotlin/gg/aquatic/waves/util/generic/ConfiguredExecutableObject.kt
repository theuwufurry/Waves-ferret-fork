package gg.aquatic.waves.util.generic

open class ConfiguredExecutableObject<B, C>(
    val executableObject: ExecutableObject<B,C>,
    val arguments: Map<String, Any?>
) {

    open fun execute(binder: B, textUpdater: (B, String) -> String): C {
        return executableObject.execute(binder, arguments, textUpdater)
    }

}