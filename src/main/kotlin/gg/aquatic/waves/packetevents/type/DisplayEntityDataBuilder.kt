package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import gg.aquatic.waves.packetevents.EntityDataBuilder
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Display.Brightness
import org.bukkit.util.Transformation

abstract class DisplayEntityDataBuilder : EntityDataBuilder() {

    fun setInterpolationDelay(delay: Int) {
        addData(8, EntityDataTypes.INT, delay)
    }

    fun setTransformationInterpolationDuration(duration: Int) {
        addData(9, EntityDataTypes.INT, duration)
    }

    fun setPosRotInterpolationDuration(duration: Int) {
        addData(10, EntityDataTypes.INT, duration)
    }

    fun setTranslation(vector: Vector3f) {
        addData(11, EntityDataTypes.VECTOR3F, vector)
    }

    fun setTranslation(x: Number, y: Number, z: Number) {
        setTranslation(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))
    }

    fun setScale(scale: Vector3f) {
        addData(12, EntityDataTypes.VECTOR3F, scale)
    }
    fun setScale(x: Number, y: Number, z: Number) {
        setScale(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))
    }
    fun setRotationLeft(rotation: Quaternion4f) {
        addData(13, EntityDataTypes.QUATERNION, rotation)
    }
    fun setRotationRight(rotation: Quaternion4f) {
        addData(14, EntityDataTypes.QUATERNION, rotation)
    }

    fun setTransformation(transformation: Transformation) {
        setScale(Vector3f(transformation.scale.x, transformation.scale.y, transformation.scale.z))
        setTranslation(Vector3f(transformation.translation.x, transformation.translation.y, transformation.translation.z))
        setRotationLeft(Quaternion4f(transformation.leftRotation.x, transformation.leftRotation.y, transformation.leftRotation.z, transformation.leftRotation.w))
        setRotationRight(Quaternion4f(transformation.rightRotation.x, transformation.rightRotation.y, transformation.rightRotation.z, transformation.rightRotation.w))
    }
    fun setBillboard(billboard: Billboard) {
        addData(15, EntityDataTypes.BYTE, billboard.ordinal.toByte())
    }
    fun setViewRange(range: Float) {
        addData(17, EntityDataTypes.FLOAT, range)
    }
    fun setShadowRadius(radius: Float) {
        addData(18, EntityDataTypes.FLOAT, radius)
    }
    fun setShadowStrength(strength: Float) {
        addData(19, EntityDataTypes.FLOAT, strength)
    }
    fun setWidth(width: Float) {
        addData(20, EntityDataTypes.FLOAT, width)
    }
    fun setHeight(height: Float) {
        addData(21, EntityDataTypes.FLOAT, height)
    }
    fun setGlowColorOverride(i: Int) {
        addData(22, EntityDataTypes.INT, i)
    }
}