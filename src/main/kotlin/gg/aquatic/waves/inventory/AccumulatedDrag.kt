package gg.aquatic.waves.inventory

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow

class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: ClickType
) {
}