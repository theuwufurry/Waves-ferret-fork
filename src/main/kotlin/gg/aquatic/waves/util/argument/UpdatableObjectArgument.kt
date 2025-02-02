package gg.aquatic.waves.util.argument

interface UpdatableObjectArgument {

    fun getUpdatedValue(updater: (String) -> String): Any?

}