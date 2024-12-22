package gg.aquatic.waves.util.block.impl

import gg.aquatic.waves.util.block.AquaticBlock
import org.bukkit.Location
import org.bukkit.block.data.BlockData

class VanillaBlock(
    override val blockData: BlockData,
    private val extra: Int? = null
): AquaticBlock() {
    override fun place(location: Location) {
        location.block.type = blockData.material
        location.block.blockData = blockData

        val blockState = location.block.state
        if (extra != null) {
            blockState.data.data = extra.toByte()
            blockState.update(true)
        }
    }
}