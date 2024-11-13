package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.math.MatrixUtil
import org.bukkit.entity.Display.Billboard
import org.bukkit.util.Transformation
import org.joml.*

abstract class DisplayEntityDataBuilder : EntityDataBuilder() {

    fun setInterpolationDelay(delay: Int): DisplayEntityDataBuilder {
        addData(8, EntityDataTypes.INT, delay)
        return this
    }

    fun setTransformationInterpolationDuration(duration: Int): DisplayEntityDataBuilder {
        addData(9, EntityDataTypes.INT, duration)
        return this
    }

    fun setPosRotInterpolationDuration(duration: Int): DisplayEntityDataBuilder {
        addData(10, EntityDataTypes.INT, duration)
        return this
    }

    fun setTranslation(vector: Vector3f): DisplayEntityDataBuilder {
        addData(11, EntityDataTypes.VECTOR3F, vector)
        return this
    }

    fun setTranslation(x: Number, y: Number, z: Number): DisplayEntityDataBuilder {
        setTranslation(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))
        return this
    }

    fun setScale(scale: Vector3f): DisplayEntityDataBuilder {
        addData(12, EntityDataTypes.VECTOR3F, scale)
        return this
    }
    fun setScale(x: Number, y: Number, z: Number): DisplayEntityDataBuilder {
        setScale(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))
        return this
    }
    fun setRotationLeft(rotation: Quaternion4f): DisplayEntityDataBuilder {
        addData(13, EntityDataTypes.QUATERNION, rotation)
        return this
    }
    fun setRotationRight(rotation: Quaternion4f): DisplayEntityDataBuilder {
        addData(14, EntityDataTypes.QUATERNION, rotation)
        return this
    }

    fun setTransformation(transformation: Transformation): DisplayEntityDataBuilder {
        setScale(Vector3f(transformation.scale.x, transformation.scale.y, transformation.scale.z))
        setTranslation(Vector3f(transformation.translation.x, transformation.translation.y, transformation.translation.z))
        setRotationLeft(Quaternion4f(transformation.leftRotation.x, transformation.leftRotation.y, transformation.leftRotation.z, transformation.leftRotation.w))
        setRotationRight(Quaternion4f(transformation.rightRotation.x, transformation.rightRotation.y, transformation.rightRotation.z, transformation.rightRotation.w))
        return this
    }

    fun setTransformation(matrix4f: Matrix4f): DisplayEntityDataBuilder {
        val f = 1.0F / matrix4f.m33()
        val triple = MatrixUtil.svdDecompose(Matrix3f(matrix4f).scale(f))
        val translation = matrix4f.getTranslation(org.joml.Vector3f()).mul(f)
        val leftRotation = Quaternionf(triple.first as Quaternionfc)
        val scale = Vector3f(triple.second as Vector3fc)
        val rightRotation = Quaternionf(triple.third as Quaternionfc)

        setTransformation(
            Transformation(
                translation,
                leftRotation,
                scale,
                rightRotation
            ))
        return this
    }

    fun setBillboard(billboard: Billboard): DisplayEntityDataBuilder {
        addData(15, EntityDataTypes.BYTE, billboard.ordinal.toByte())
        return this
    }
    fun setViewRange(range: Float): DisplayEntityDataBuilder {
        addData(17, EntityDataTypes.FLOAT, range)
        return this
    }
    fun setShadowRadius(radius: Float): DisplayEntityDataBuilder {
        addData(18, EntityDataTypes.FLOAT, radius)
        return this
    }
    fun setShadowStrength(strength: Float): DisplayEntityDataBuilder {
        addData(19, EntityDataTypes.FLOAT, strength)
        return this
    }
    fun setWidth(width: Float): DisplayEntityDataBuilder {
        addData(20, EntityDataTypes.FLOAT, width)
        return this
    }
    fun setHeight(height: Float): DisplayEntityDataBuilder {
        addData(21, EntityDataTypes.FLOAT, height)
        return this
    }
    fun setGlowColorOverride(i: Int): DisplayEntityDataBuilder {
        addData(22, EntityDataTypes.INT, i)
        return this
    }
}