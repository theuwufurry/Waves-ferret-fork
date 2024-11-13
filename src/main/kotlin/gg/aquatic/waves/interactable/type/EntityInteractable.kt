package gg.aquatic.waves.interactable.type

import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.interactable.Interactable
import org.bukkit.Location
import org.bukkit.entity.Player

class EntityInteractable(val entity: FakeEntity) : Interactable() {

    override val location: Location
        get() {
            return entity.location
        }
    override val viewers: MutableSet<Player>
        get() {
            return entity.viewers
        }

    override fun addViewer(player: Player) {
        entity.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        entity.removeViewer(player)
    }
}