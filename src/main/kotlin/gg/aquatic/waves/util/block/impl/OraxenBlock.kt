package gg.aquatic.waves.util.block.impl

import gg.aquatic.waves.util.block.AquaticBlock
import io.th0rgal.oraxen.api.OraxenBlocks
import org.bukkit.Location
import org.bukkit.block.data.BlockData

class OraxenBlock(
    val oraxenId: String
): AquaticBlock() {
    override fun place(location: Location) {
        OraxenBlocks.place(oraxenId,location)
    }

    override val blockData: BlockData
        get() {
            return OraxenBlocks.getOraxenBlockData(oraxenId)!!
        }
}