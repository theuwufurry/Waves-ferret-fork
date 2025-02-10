package gg.aquatic.waves.input

import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface Input {

    val awaiting: Map<Player, AwaitingInput>

    fun forceCancel(player: Player) {
        awaiting[player]?.handle?.forceCancel(player)
    }
}

interface InputHandle {
    val input: Input
    fun await(player: Player): CompletableFuture<String?>
    fun forceCancel(player: Player)

}

class AwaitingInput(
    val player: Player,
    val future: CompletableFuture<String?>,
    val handle: InputHandle
)