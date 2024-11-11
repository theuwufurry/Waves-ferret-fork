package gg.aquatic.waves.util.math

import org.joml.Math
import org.joml.Matrix3f
import org.joml.Quaternionf

class GivensParameters(val sinHalf: Float, val cosHalf: Float) {

    companion object {
        fun fromUnnormalized(var0: Float, var1: Float): GivensParameters {
            val var2 = Math.invsqrt(var0 * var0 + var1 * var1)
            return GivensParameters(var2 * var0, var2 * var1)
        }

        fun fromPositiveAngle(var0: Float): GivensParameters {
            val var1 = Math.sin(var0 / 2.0f)
            val var2 = Math.cosFromSin(var1, var0 / 2.0f)
            return GivensParameters(var1, var2)
        }
    }


    fun inverse(): GivensParameters {
        return GivensParameters(-this.sinHalf, this.cosHalf)
    }

    fun aroundX(var0: Quaternionf): Quaternionf {
        return var0.set(this.sinHalf, 0.0f, 0.0f, this.cosHalf)
    }

    fun aroundY(var0: Quaternionf): Quaternionf {
        return var0.set(0.0f, this.sinHalf, 0.0f, this.cosHalf)
    }

    fun aroundZ(var0: Quaternionf): Quaternionf {
        return var0.set(0.0f, 0.0f, this.sinHalf, this.cosHalf)
    }

    fun cos(): Float {
        return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf
    }

    fun sin(): Float {
        return 2.0f * this.sinHalf * this.cosHalf
    }

    fun aroundX(var0: Matrix3f): Matrix3f {
        var0.m01 = 0.0f
        var0.m02 = 0.0f
        var0.m10 = 0.0f
        var0.m20 = 0.0f
        val var1 = this.cos()
        val var2 = this.sin()
        var0.m11 = var1
        var0.m22 = var1
        var0.m12 = var2
        var0.m21 = -var2
        var0.m00 = 1.0f
        return var0
    }

    fun aroundY(var0: Matrix3f): Matrix3f {
        var0.m01 = 0.0f
        var0.m10 = 0.0f
        var0.m12 = 0.0f
        var0.m21 = 0.0f
        val var1 = this.cos()
        val var2 = this.sin()
        var0.m00 = var1
        var0.m22 = var1
        var0.m02 = -var2
        var0.m20 = var2
        var0.m11 = 1.0f
        return var0
    }

    fun aroundZ(var0: Matrix3f): Matrix3f {
        var0.m02 = 0.0f
        var0.m12 = 0.0f
        var0.m20 = 0.0f
        var0.m21 = 0.0f
        val var1 = this.cos()
        val var2 = this.sin()
        var0.m00 = var1
        var0.m11 = var1
        var0.m01 = var2
        var0.m10 = -var2
        var0.m22 = 1.0f
        return var0
    }

    fun sinHalf(): Float {
        return this.sinHalf
    }

    fun cosHalf(): Float {
        return this.cosHalf
    }

}