//package gg.aquatic.waves.interactable.type
//
////import com.ticxo.modelengine.api.ModelEngineAPI
////import com.ticxo.modelengine.api.model.ActiveModel
////import com.ticxo.modelengine.api.model.ModeledEntity
////import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes
//import gg.aquatic.waves.util.audience.AquaticAudience
//import gg.aquatic.waves.interactable.Interactable
//import gg.aquatic.waves.interactable.InteractableHandler
//import gg.aquatic.waves.interactable.InteractableInteractEvent
//import gg.aquatic.waves.interactable.MEGInteractableDummy
//import org.bukkit.Bukkit
//import org.bukkit.Location
//import org.bukkit.entity.Player
//import kotlin.jvm.optionals.getOrNull
//
//class MEGInteractable(
//    override val location: Location, val modelId: String, audience: AquaticAudience, override val onInteract: (InteractableInteractEvent) -> Unit,
//) : Interactable() {
//
//    override val viewers: MutableSet<Player> = mutableSetOf()
//
//    override var audience: AquaticAudience = audience
//        set(value) {
//            field = value
//            for (player in viewers.toList()) {
//                if (!field.canBeApplied(player)) {
//                    removeViewer(player)
//                }
//            }
//            for (player in
//            Bukkit.getOnlinePlayers().filter { !viewers.contains(it) }) {
//                if (!field.canBeApplied(player)) continue
//                addViewer(player)
//            }
//        }
//
//    val dummy = MEGInteractableDummy(this).apply {
//        location = this@MEGInteractable.location
//        bodyRotationController.yBodyRot = location.yaw
//        bodyRotationController.xHeadRot = location.pitch
//        bodyRotationController.yHeadRot = location.yaw
//        yHeadRot = location.yaw
//        yBodyRot = location.yaw
//        isDetectingPlayers = false
//    }
//
//    fun setSkin(player: Player) {
//        activeModel?.apply {
//            for (value in bones.values) {
//                value.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent {
//                    setSkin(player)
//                }
//            }
//        }
//    }
//
//    val modeledEntity: ModeledEntity?
//        get() {
//            return ModelEngineAPI.getModeledEntity(dummy.uuid)
//        }
//    val activeModel: ActiveModel?
//        get() {
//            return modeledEntity?.getModel(modelId)?.getOrNull()
//        }
//
//    init {
//        this.audience = audience
//        val modeledEntity = ModelEngineAPI.createModeledEntity(dummy)
//        val activeModel = ModelEngineAPI.createActiveModel(modelId)
//        InteractableHandler.megInteractables += this
//        modeledEntity.addModel(activeModel, true)
//    }
//
//
//    override fun addViewer(player: Player) {
//        viewers.add(player)
//        dummy.setForceViewing(player, true)
//    }
//
//    override fun removeViewer(player: Player) {
//        viewers.remove(player)
//        dummy.setForceViewing(player, false)
//    }
//
//
//    override fun destroy() {
//        this.activeModel?.destroy()
//        this.activeModel?.isRemoved = true
//        dummy.isRemoved = true
//        InteractableHandler.megInteractables -= this
//        viewers.clear()
//    }
//
//    override fun updateViewers() {
//        location.world?.players?.forEach { player ->
//            if (audience.canBeApplied(player)) {
//                addViewer(player)
//            }
//        }
//    }
//}