package gg.aquatic.waves.util

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import io.netty.buffer.PooledByteBufAllocator
import org.bukkit.Bukkit
import org.bukkit.entity.Entity


fun Entity.getEntityData(): List<EntityData> {
    val nmsEntity = SpigotReflectionUtil.getNMSEntity(this)

    val synchedDataMethod = SpigotReflectionUtil.NMS_ENTITY_CLASS.getDeclaredMethod("ar")
    val synchedData = synchedDataMethod.invoke(nmsEntity)
    Bukkit.broadcastMessage("SynchedData: ${synchedData.javaClass.name}")
    val listField = synchedData.javaClass.getDeclaredField("e")
    Bukkit.broadcastMessage("Data List: ${listField.name}")
    listField.isAccessible = true
    val list = listField.get(synchedData) as Array<*>
    Bukkit.broadcastMessage("List: ${list.size}")

    val buffer = PooledByteBufAllocator.DEFAULT.buffer()
    val registryByteBuf = SpigotReflectionUtil.createPacketDataSerializer(buffer)
    for (entry in list) {
        val valueMethod = entry?.javaClass?.getDeclaredMethod("e") ?: continue
        val value = valueMethod.invoke(entry) ?: continue
        val writeMethod = value.javaClass.getDeclaredMethod("a", registryByteBuf.javaClass)
        writeMethod.invoke(
            value,
            registryByteBuf
        )
    }
    ByteBufHelper.writeByte(buffer, 255)
    val entityData = readEntityMetadata(buffer)
    Bukkit.broadcastMessage("Values: ${entityData.size}")
    return entityData
}


fun readEntityMetadata(buffer: Any): List<EntityData> {
    val wrapper = PacketWrapper(WrapperPlayServerEntityMetadata(0, mutableListOf()).nativePacketId)
    wrapper.setBuffer(buffer)
    val data = wrapper.readEntityMetadata()
    return data
}