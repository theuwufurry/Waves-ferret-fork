package gg.aquatic.waves.inventory2

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import gg.aquatic.waves.Waves
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.inventory.ClickType
import gg.aquatic.waves.inventory.InventoryViewer
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

object InventoryManager : WaveModule {

    val openedInventories = ConcurrentHashMap<Player, PacketInventory>()
    override val type: WaveModules = WaveModules.INVENTORIES

    override fun initialize(waves: Waves) {
        packetEvent<PacketReceiveEvent> {
            val player = player() ?: return@packetEvent

            if (packetType != PacketType.Play.Client.CLICK_WINDOW) return@packetEvent
            val packet = WrapperPlayClientClickWindow(this)
            if (packet.windowId != 126) return@packetEvent

            isCancelled = true
            val inventory = openedInventories[player] ?: return@packetEvent
            val viewer = inventory.viewers[player.uniqueId] ?: return@packetEvent

            Bukkit.broadcastMessage("Slot: ${packet.slot}")
            Bukkit.broadcastMessage("Slots:")
            packet.slots.orElse(emptyMap()).forEach { (slot, item) ->
                Bukkit.broadcastMessage("- $slot: ${SpigotConversionUtil.toBukkitItemStack(item).type.name}")
            }
            Bukkit.broadcastMessage("Button: ${packet.button}")
            Bukkit.broadcastMessage("ActionKey: ${packet.actionNumber.orElse(-999)}")
            Bukkit.broadcastMessage("ClickType: ${packet.windowClickType.name}")

            val type = getClickType(packet, viewer)
            Bukkit.broadcastMessage("ButtonType: ${type.first} ClickType: ${type.second}")

            if (type.second == ClickType.PICKUP && type.first == ButtonType.RIGHT) {
                Bukkit.broadcastMessage("Setting carried item to instance")
                viewer.carriedItem = packet.carriedItemStack
            } else if (type.second == ClickType.PLACE && packet.carriedItemStack == com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY || SpigotConversionUtil.toBukkitItemStack(packet.carriedItemStack).type == Material.AIR) {
                Bukkit.broadcastMessage("Setting carried item to nothing")
                viewer.carriedItem = null
            }
        }
        packetEvent<PacketSendEvent> {
            val player = player() ?: return@packetEvent
            if (packetType == PacketType.Play.Server.WINDOW_ITEMS) {
                val packet = WrapperPlayServerWindowItems(this)
                val inventory = openedInventories[player] ?: return@packetEvent
                isCancelled = true
            } else if (packetType == PacketType.Play.Server.SET_SLOT) {
                val packet = WrapperPlayServerSetSlot(this)
                val inventory = openedInventories[player] ?: return@packetEvent
                if (packet.windowId == 0) {
                    val menuContent = inventory.content
                    val menuSlot = menuSlotFromPlayerSlot(packet.slot, inventory)
                    if (menuContent[menuSlot] != null) {
                        isCancelled = true
                        Bukkit.broadcastMessage("Cancelling!")
                        return@packetEvent
                    }
                }
            }
        }
    }

    private fun menuSlotFromPlayerSlot(slot: Int, inventory: PacketInventory): Int {
        return if (slot < 9) {
            slot + 27
        } else {
            slot - 9
        } + inventory.type.lastIndex
    }

    private fun playerSlotFromMenuSlot(slot: Int, inventory: PacketInventory): Int {
        return if (slot - inventory.type.lastIndex < 27) slot - inventory.type.lastIndex + 9 else slot - inventory.type.lastIndex - 27
    }

    override fun disable(waves: Waves) {

    }

    fun openMenu(player: Player, inventory: PacketInventory) {
        openedInventories[player] = inventory
        val viewer = InventoryViewer(player)
        inventory.viewers[player.uniqueId] = viewer

        player.toUser().let {
            it.sendPacket(inventory.inventoryOpenPacket)
            val items = ArrayList<com.github.retrooper.packetevents.protocol.item.ItemStack?>()
            for (i in 0 until inventory.type.size + 35) {

                val contentItem = inventory.content[i]
                if (contentItem == null && i >= inventory.type.size) {
                    val playerItemIndex = playerSlotFromMenuSlot(i, inventory)
                    val playerItem = player.inventory.getItem(playerItemIndex)
                    items += if (playerItem == null) null else SpigotConversionUtil.fromBukkitItemStack(playerItem)
                    continue
                }
                items += inventory.content[i]?.let {
                    SpigotConversionUtil.fromBukkitItemStack(it)
                }
            }
            val packet = WrapperPlayServerWindowItems(
                126,
                0,
                items,
                viewer.carriedItem
            )
            it.sendPacket(packet)
        }
    }

    fun updateItem(inventory: PacketInventory, item: ItemStack, slot: Int) {
        if (slot > inventory.type.size + 36) return
        inventory.addItem(slot, item)

        val packet = WrapperPlayServerSetSlot(
            126,
            0,
            slot,
            SpigotConversionUtil.fromBukkitItemStack(
                item
            )
        )
        for ((_, viewer) in inventory.viewers) {
            viewer.player.toUser().sendPacket(packet)
        }
    }

    fun updateItems(inventory: PacketInventory, items: HashMap<Int, ItemStack>) {
        for ((slot, item) in items) {
            inventory.addItem(slot, item)
        }

        val items = ArrayList<com.github.retrooper.packetevents.protocol.item.ItemStack?>()
        for (i in 0 until inventory.type.size + 36) {
            items += inventory.content[i]?.let {
                SpigotConversionUtil.fromBukkitItemStack(it)
            }
        }
        for ((_, viewer) in inventory.viewers) {
            val packet = WrapperPlayServerWindowItems(
                126,
                0,
                items,
                viewer.carriedItem
            )
            viewer.player.toUser().sendPacket(packet)
        }
    }

    fun getClickType(packet: WrapperPlayClientClickWindow, viewer: InventoryViewer): Pair<ButtonType, ClickType> {
        return when (packet.windowClickType) {
            WindowClickType.PICKUP -> {
                val cursorItem = viewer.carriedItem?.let { SpigotConversionUtil.toBukkitItemStack(it) }
                Bukkit.broadcastMessage("Cursor: ${cursorItem?.type}")
                if (packet.carriedItemStack != com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY && SpigotConversionUtil.toBukkitItemStack(packet.carriedItemStack).type != Material.AIR) {
                    Bukkit.broadcastMessage("It is not empty")
                    if (packet.button == 0) Pair(ButtonType.LEFT, ClickType.PICKUP)
                    else if (cursorItem != null && cursorItem.type != Material.AIR) Pair(ButtonType.RIGHT, ClickType.PLACE)
                    else Pair(ButtonType.RIGHT, ClickType.PICKUP)
                } else {
                    Bukkit.broadcastMessage("It is empty")
                    if (packet.button == 0) Pair(ButtonType.LEFT, ClickType.PLACE)
                    else Pair(ButtonType.RIGHT, if (cursorItem?.type == Material.AIR) ClickType.PICKUP else ClickType.PLACE)
                }
            }

            WindowClickType.QUICK_MOVE -> {
                if (packet.button == 0) {
                    Pair(ButtonType.SHIFT_LEFT, ClickType.SHIFT_CLICK)
                } else {
                    Pair(ButtonType.SHIFT_RIGHT, ClickType.SHIFT_CLICK)
                }
            }

            WindowClickType.SWAP -> {
                when (packet.button) {
                    40 -> Pair(ButtonType.F, ClickType.PICKUP)
                    0 -> Pair(ButtonType.NUM_0, ClickType.PICKUP)
                    1 -> Pair(ButtonType.NUM_1, ClickType.PICKUP)
                    2 -> Pair(ButtonType.NUM_2, ClickType.PICKUP)
                    3 -> Pair(ButtonType.NUM_3, ClickType.PICKUP)
                    4 -> Pair(ButtonType.NUM_4, ClickType.PICKUP)
                    5 -> Pair(ButtonType.NUM_5, ClickType.PICKUP)
                    6 -> Pair(ButtonType.NUM_6, ClickType.PICKUP)
                    7 -> Pair(ButtonType.NUM_7, ClickType.PICKUP)
                    8 -> Pair(ButtonType.NUM_8, ClickType.PICKUP)
                    else -> Pair(ButtonType.LEFT, ClickType.PLACE)
                }
            }

            WindowClickType.CLONE -> {
                Pair(ButtonType.MIDDLE, ClickType.PICKUP)
            }

            WindowClickType.THROW -> {
                if (packet.button == 0) {
                    Pair(ButtonType.DROP, ClickType.PICKUP)
                } else {
                    Pair(ButtonType.CTRL_DROP, ClickType.PICKUP)
                }
            }

            WindowClickType.QUICK_CRAFT -> {
                when (packet.button) {
                    0 -> Pair(ButtonType.LEFT, ClickType.DRAG_START)
                    4 -> Pair(ButtonType.RIGHT, ClickType.DRAG_START)
                    8 -> Pair(ButtonType.MIDDLE, ClickType.DRAG_START)

                    1 -> Pair(ButtonType.LEFT, ClickType.DRAG_ADD)
                    5 -> Pair(ButtonType.RIGHT, ClickType.DRAG_ADD)
                    9 -> Pair(ButtonType.MIDDLE, ClickType.DRAG_ADD)

                    2 -> Pair(ButtonType.LEFT, ClickType.DRAG_END)
                    6 -> Pair(ButtonType.RIGHT, ClickType.DRAG_END)
                    10 -> Pair(ButtonType.MIDDLE, ClickType.DRAG_END)

                    else -> Pair(ButtonType.LEFT, ClickType.UNDEFINED)
                }
            }

            WindowClickType.PICKUP_ALL -> {
                Pair(ButtonType.DOUBLE_CLICK, ClickType.PICKUP_ALL)
            }

            else -> {
                Pair(ButtonType.LEFT, ClickType.UNDEFINED)
            }
        }
    }
}