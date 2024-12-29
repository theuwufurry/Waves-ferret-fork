package gg.aquatic.waves.registry

import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.price.AbstractPrice
import gg.aquatic.waves.util.requirement.AbstractRequirement
import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.interactable.settings.BlockInteractableSettings
import gg.aquatic.waves.interactable.settings.EntityInteractableSettings
import gg.aquatic.waves.interactable.settings.MEGInteractableSettings
import gg.aquatic.waves.interactable.settings.OraxenEntityInteractableSettings
import gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty
import gg.aquatic.waves.interactable.settings.entityproperty.display.DisplayEntityProperty
import gg.aquatic.waves.interactable.settings.entityproperty.display.ItemDisplayEntityProperty
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.factory.*
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.packetevents.type.BaseEntityDataBuilder
import gg.aquatic.waves.util.action.*
import gg.aquatic.waves.util.action.impl.discord.DiscordWebhookAction
import gg.aquatic.waves.util.action.impl.*
import gg.aquatic.waves.util.currency.Currency
import gg.aquatic.waves.util.price.impl.ItemPrice
import gg.aquatic.waves.util.price.impl.VaultPrice
import gg.aquatic.waves.util.requirement.impl.ExpressionPlayerRequirement
import gg.aquatic.waves.util.toMMComponent
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

object WavesRegistry {

    val INDEX_TO_CURRENCY = HashMap<Int, RegisteredCurrency>()
    val ECONOMY = HashMap<String, Currency>()
    val ACTION = HashMap<Class<*>, MutableMap<String, AbstractAction<*>>>().apply {
        val p = getOrPut(Player::class.java) { HashMap() }
        p["actionbar"] = ActionbarAction()
        p["bossbar"] = BossbarAction()
        p["broadcast"] = BroadcastAction()
        p["command"] = CommandAction()
        p["giveitem"] = GiveItemAction()
        p["message"] = MessageAction()
        p["title"] = TitleAction()
        p["sound"] = SoundAction()
        p["discord-webhook"] = DiscordWebhookAction()
    }
    val REQUIREMENT = HashMap<Class<*>, MutableMap<String, AbstractRequirement<*>>>().apply {
        val p = getOrPut(Player::class.java) { HashMap() }
        p += "expression" to ExpressionPlayerRequirement()
    }
    val PRICE by lazy {
        HashMap<Class<*>, MutableMap<String, AbstractPrice<*>>>().apply {
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
        "ITEMSADDER" to IAFactory,
        "ECO" to EcoFactory
    )

    val INTERACTABLE_FACTORIES = hashMapOf(
        "ORAXEN_FURNITURE" to OraxenEntityInteractableSettings.Companion,
        "ENTITY" to EntityInteractableSettings.Companion,
        "BLOCK" to BlockInteractableSettings.Companion,
        "MODELENGINE" to MEGInteractableSettings.Companion
    )

    val ENTITY_PROPERTY_FACTORIES = hashMapOf(
        createProperty("is-on-fire") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isOnFire(s.getBoolean(str))
        },
        createProperty("is-sneaking") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isSneaking(s.getBoolean(str))
        },
        createProperty("is-sprinting") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isSprinting(s.getBoolean(str))
        },
        createProperty("is-swimming") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isSwimming(s.getBoolean(str))
        },
        createProperty("invisible") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isInvisible(s.getBoolean(str))
        },
        createProperty("glowing") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isGlowing(s.getBoolean(str))
        },
        createProperty("is-flying") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isFlying(s.getBoolean(str))
        },
        createProperty("custom-name") { s, str, builder ->
            (builder as BaseEntityDataBuilder).setCustomName((s.getString(str) ?: "").toMMComponent())
        },
        createProperty("custom-name-visible") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isCustomNameVisible(s.getBoolean(str))
        },
        createProperty("is-silent") { s, str, builder ->
            (builder as BaseEntityDataBuilder).isSilent(s.getBoolean(str))
        },
        createProperty("no-gravity") { s, str, builder ->
            (builder as BaseEntityDataBuilder).hasNoGravity(s.getBoolean(str))
        },
        createProperty("pose") { s, str, builder ->
            (builder as BaseEntityDataBuilder).setPose(EntityPose.valueOf(s.getString(str, "STANDING")!!.uppercase()))
        },
        "item" to ItemDisplayEntityProperty.Item.Companion,
        "item-transform" to ItemDisplayEntityProperty.ItemTransformation.Companion,
        "billboard" to DisplayEntityProperty.Billboard.Companion,
        "interpolation-delay" to DisplayEntityProperty.InterpolationDelay.Companion,
        "interpolation-duration" to DisplayEntityProperty.InterpolationDuration.Companion,
        "teleport-interpolation-duration" to DisplayEntityProperty.TeleportInterpolationDuration.Companion,
        "transformation" to DisplayEntityProperty.Transformation.Companion,
    )
    val ITEM = HashMap<String, AquaticItem>()

    private fun createProperty(
        path: String,
        factory: (ConfigurationSection, String, EntityDataBuilder) -> Unit
    ): Pair<String, EntityProperty.Serializer> {
        return path to object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                return object : EntityProperty {
                    override fun apply(builder: EntityDataBuilder) {
                        factory(section, path, builder)
                    }
                }
            }
        }
    }
}