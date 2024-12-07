package gg.aquatic.waves.inventory

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import org.bukkit.entity.Player

data class InventoryClickData(
    val player: Player,
    val wrapper: WrapperPlayClientClickWindow,
    val clickType: ClickType,
    val buttonType: ButtonType
)