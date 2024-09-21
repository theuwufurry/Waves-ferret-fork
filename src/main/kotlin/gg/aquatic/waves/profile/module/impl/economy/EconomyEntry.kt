package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.aquaticseries.lib.util.toBytes
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.*

class EconomyEntry(
    uuid: UUID,
) : ProfileModuleEntry(uuid) {

    val balance = HashMap<String, Double>()
    private var modified = false

    override fun save(connection: Connection) {
        if (!modified) {
            return
        }

        connection.prepareStatement("replace into aquaticcurrency values (?, ?, ?)").use { preparedStatement ->
            for ((id, balance) in balance) {
                preparedStatement.setBytes(1, uuid.toBytes())
                preparedStatement.setString(2, id)
                preparedStatement.setDouble(3, balance)
                preparedStatement.addBatch()
            }
            preparedStatement.executeBatch()
        }
        modified = false
    }
}