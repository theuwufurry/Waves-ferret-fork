package gg.aquatic.waves.menu.settings

import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.MenuComponent

interface IButtonSettings {

    fun create(updater: (String, AquaticMenu) -> String): MenuComponent

}