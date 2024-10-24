package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.economy.Currency
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.item2.factories.HDBFactory
import gg.aquatic.aquaticseries.lib.item2.factories.IAFactory
import gg.aquatic.aquaticseries.lib.item2.factories.MMFactory
import gg.aquatic.aquaticseries.lib.item2.factories.OraxenFactory
import gg.aquatic.aquaticseries.lib.price.AbstractPrice
import gg.aquatic.aquaticseries.lib.requirement.AbstractRequirement
import gg.aquatic.waves.Waves
import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.util.action.*
import gg.aquatic.waves.util.price.ItemPrice
import gg.aquatic.waves.util.price.VaultPrice
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object WavesRegistry {

    val INDEX_TO_CURRENCY = HashMap<Int, RegisteredCurrency>()
    val ECONOMY = HashMap<String,Currency>()
    val ACTION = HashMap<Class<*>,MutableMap<String,AbstractAction<*>>>().apply {
        val p = getOrPut(Player::class.java) { HashMap() }
        p["actionbar"] = ActionbarAction()
        p["bossbar"] = BossbarAction()
        p["broadcast"] = BroadcastAction()
        p["command"] = CommandAction()
        p["giveitem"] = GiveItemAction()
        p["message"] = MessageAction()
        p["title"] = TitleAction()
        p["sound"] = SoundAction()

    }
    val REQUIREMENT = HashMap<Class<*>,MutableMap<String,AbstractRequirement<*>>>()
    val PRICE by lazy {
        HashMap<Class<*>,MutableMap<String,AbstractPrice<*>>>().apply {
            val p = getOrPut(Player::class.java) { HashMap() }
            p["item"] = ItemPrice()
            if (Bukkit.getPluginManager().getPlugin("Vault") != null)
                p["vault"] = VaultPrice()
        }
    }
    val ITEM_FACTORIES = hashMapOf(
        "MYTHICITEM" to MMFactory,
        "ORAXEN" to OraxenFactory,
        "HDB" to HDBFactory,
        "ITEMSADDER" to IAFactory
    )
    val ITEM = HashMap<String, AquaticItem>()

}