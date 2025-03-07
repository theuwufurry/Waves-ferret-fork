package gg.aquatic.waves.interactable.settings

import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import gg.aquatic.waves.fake.npc.FakePlayer
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.interactable.settings.entityproperty.EntityArmorProperty
import gg.aquatic.waves.interactable.type.NPCInteractable
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.toMMComponent
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class NPCInteractableSettings(
    val userProfile: UserProfile,
    val tabName: String?,
    val prefixName: String?,
    val namedTextColor: NamedTextColor,
    val suffixName: String?,
    val gameMode: GameMode,
    val offset: Vector,
    val yawPitch: Pair<Float, Float>,
    val equipment: EntityArmorProperty
) : InteractableSettings {
    override fun build(
        location: Location,
        audience: AquaticAudience,
        onInteract: (InteractableInteractEvent) -> Unit
    ): NPCInteractable {
        val fakePlayer = FakePlayer(
            userProfile,
            tabName?.toMMComponent(),
            namedTextColor,
            prefixName?.toMMComponent(),
            suffixName?.toMMComponent(),
            gameMode,
            location.clone().add(offset).apply {
                yaw = yawPitch.first
                pitch = yawPitch.second
            },
            50,
            audience
        )
        fakePlayer.npc.helmet = SpigotConversionUtil.fromBukkitItemStack(equipment.helmet?.getItem() ?: ItemStack(Material.AIR))
        fakePlayer.npc.chestplate = SpigotConversionUtil.fromBukkitItemStack(equipment.chestplate?.getItem() ?: ItemStack(Material.AIR))
        fakePlayer.npc.leggings = SpigotConversionUtil.fromBukkitItemStack(equipment.leggings?.getItem() ?: ItemStack(Material.AIR))
        fakePlayer.npc.boots = SpigotConversionUtil.fromBukkitItemStack(equipment.boots?.getItem() ?: ItemStack(Material.AIR))
        fakePlayer.npc.mainHand = SpigotConversionUtil.fromBukkitItemStack(equipment.mainHand?.getItem() ?: ItemStack(Material.AIR))
        fakePlayer.npc.offHand = SpigotConversionUtil.fromBukkitItemStack(equipment.offHand?.getItem() ?: ItemStack(Material.AIR))

        fakePlayer.npc.updateEquipment()

        fakePlayer.register()

        return NPCInteractable(
            fakePlayer,
            onInteract
        )
    }

    companion object : InteractableSettingsFactory {
        override fun load(section: ConfigurationSection): NPCInteractableSettings? {
            val textures = ArrayList<TextureProperty>()

            val skin = section.getString("skin.value") ?: return null
            val signature = section.getString("skin.signature") ?: return null
            textures.add(TextureProperty("textures", skin, signature))

            val npcName = section.getString("name") ?: return null

            val userProfile = UserProfile(
                UUID.randomUUID(),
                npcName,
                textures,
            )
            val tabName = section.getString("tab-name")
            val prefixName = section.getString("prefix-name")
            val namedTextColor = NamedTextColor.NAMES.value(section.getString("named-text-color")?.uppercase() ?: "WHITE") ?: NamedTextColor.WHITE
            val suffixName = section.getString("suffix-name")
            val gamemode = GameMode.valueOf(section.getString("gamemode")?.uppercase() ?: "CREATIVE")

            val offsetStrs = section.getString("offset", "0;0;0")!!.split(";")
            val offset = Vector(
                offsetStrs.getOrElse(0) { "0" }.toDouble(),
                offsetStrs.getOrElse(1) { "0" }.toDouble(),
                offsetStrs.getOrElse(2) { "0" }.toDouble()
            )
            val equipment = EntityArmorProperty.Serializer.load(section)
            val yawPitch = (
                    offsetStrs.getOrElse(3) {"0"}.toFloat()
                    ) to (
                    offsetStrs.getOrElse(4) {"0"}.toFloat())
            return NPCInteractableSettings(
                userProfile,
                tabName,
                prefixName,
                namedTextColor,
                suffixName,
                gamemode,
                offset,
                yawPitch,
                equipment
            )
        }

    }
}