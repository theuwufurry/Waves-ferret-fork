package gg.aquatic.waves.interactable.type

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.MEGInteractableDummy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class MEGInteractable(
    val modelId: String, audience: AquaticAudience,
) : Interactable() {

    override val viewers: MutableSet<Player> = mutableSetOf()

    override var audience: AquaticAudience = audience
        set(value) {
            field = value
            for (player in viewers.toList()) {
                if (!field.canBeApplied(player)) {
                    removeViewer(player)
                }
            }
            for (player in
            Bukkit.getOnlinePlayers().filter { !viewers.contains(it) }) {
                if (!field.canBeApplied(player)) continue
                addViewer(player)
            }
        }

    val dummy = MEGInteractableDummy(this).apply {
        isDetectingPlayers = false
    }
    val modeledEntity: ModeledEntity
        get() {
            return ModelEngineAPI.getModeledEntity(dummy.uuid)
        }
    val activeModel: ActiveModel
        get() {
            return modeledEntity.getModel(modelId).get()
        }

    init {
        this.audience = audience
        val modeledEntity = ModelEngineAPI.createModeledEntity(dummy)
        val activeModel = ModelEngineAPI.createActiveModel(modelId)
        modeledEntity.addModel(activeModel, true)
    }

    override val location: Location
        get() {
            return dummy.location
        }

    override fun addViewer(player: Player) {
        viewers.add(player)
        dummy.setForceViewing(player, true)
    }

    override fun removeViewer(player: Player) {
        viewers.remove(player)
        dummy.setForceViewing(player, false)
    }
}