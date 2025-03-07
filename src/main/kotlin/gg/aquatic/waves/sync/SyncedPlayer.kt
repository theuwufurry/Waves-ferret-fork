package gg.aquatic.waves.sync

import java.util.*

class SyncedPlayer(
    val uuid: UUID,
    val userName: String,
    val server: String?,
    val data: HashMap<String, String>
) {
}