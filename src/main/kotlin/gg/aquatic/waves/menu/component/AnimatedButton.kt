package gg.aquatic.waves.menu.component

import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.item.modifyFastMeta
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.MenuComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import java.util.TreeMap

class AnimatedButton(
    override val id: String,
    val frames: TreeMap<Int,MenuComponent>,
    //slots: Collection<Int>,
    priority: Int,
    val updateEvery: Int,
    failComponent: MenuComponent?,
    viewRequirements: (AquaticMenu) -> Boolean = { true },
    textUpdater: (String, AquaticMenu) -> String = { s, _ -> s },
    onClick: (AsyncPacketInventoryInteractEvent) -> Unit = { _ -> }
) : MenuComponent() {

    override var priority: Int = priority
        private set
        get() {
            if (currentComponent == null) {
                return field
            }
            return currentComponent?.priority ?: field
        }
    override var slots: Collection<Int> = listOf()
        private set
        get() {
            if (currentComponent == null) {
                return currentFrame.slots
            }
            return currentComponent?.slots ?: currentFrame.slots
        }
    override var onClick: (AsyncPacketInventoryInteractEvent) -> Unit = onClick
        private set
        get() {
            if (currentComponent == null) {
                return field
            }
            return currentComponent?.onClick ?: { _ -> }
        }

    var viewRequirements: (AquaticMenu) -> Boolean = viewRequirements
        private set
    var textUpdater: (String, AquaticMenu) -> String = textUpdater
        private set
    var failComponent: MenuComponent? = failComponent
        private set

    private var currentComponent: MenuComponent? = null

    private var currentFrame = frames.firstEntry().value
    override fun itemstack(menu: AquaticMenu): ItemStack? {
        if (!viewRequirements(menu)) {
            currentComponent = failComponent
            return currentComponent?.itemstack(menu)
        }
        return currentFrame.itemstack(menu)
    }

    private var animationTick = 0
    private var tick = 0
    override fun tick(menu: AquaticMenu) {
        if (frames.containsKey(animationTick)) {
            currentFrame = frames[animationTick]
        }
        if (tick >= updateEvery) {
            tick = 0
            menu.updateComponent(this)
        }
        animationTick++
        tick++
    }
}