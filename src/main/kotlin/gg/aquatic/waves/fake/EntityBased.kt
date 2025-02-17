package gg.aquatic.waves.fake

import gg.aquatic.waves.fake.entity.FakeEntityInteractEvent

interface EntityBased {

    val entityId: Int
    var onInteract: (FakeEntityInteractEvent) -> Unit
}