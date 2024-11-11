package gg.aquatic.waves.util.math

import org.joml.Math
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.hypot

object MatrixUtil {

    private var G = 3.0f + 2.0f * Math.sqrt(2.0f)
    private var PI_4 = GivensParameters.fromPositiveAngle(0.7853982f)

    private fun stepJacobi(var0: Matrix3f, var1: Matrix3f, var2: Quaternionf, var3: Quaternionf) {
        var var4: GivensParameters
        var var5: Quaternionf
        if (var0.m01 * var0.m01 + var0.m10 * var0.m10 > 1.0E-6f) {
            var4 = approxGivensQuat(var0.m00, 0.5f * (var0.m01 + var0.m10), var0.m11)
            var5 = var4.aroundZ(var2)
            var3.mul(var5)
            var4.aroundZ(var1)
            similarityTransform(var0, var1)
        }

        if (var0.m02 * var0.m02 + var0.m20 * var0.m20 > 1.0E-6f) {
            var4 = approxGivensQuat(var0.m00, 0.5f * (var0.m02 + var0.m20), var0.m22).inverse()
            var5 = var4.aroundY(var2)
            var3.mul(var5)
            var4.aroundY(var1)
            similarityTransform(var0, var1)
        }

        if (var0.m12 * var0.m12 + var0.m21 * var0.m21 > 1.0E-6f) {
            var4 = approxGivensQuat(var0.m11, 0.5f * (var0.m12 + var0.m21), var0.m22)
            var5 = var4.aroundX(var2)
            var3.mul(var5)
            var4.aroundX(var1)
            similarityTransform(var0, var1)
        }
    }

    fun eigenvalueJacobi(var0: Matrix3f, var1: Int): Quaternionf {
        val var2 = Quaternionf()
        val var3 = Matrix3f()
        val var4 = Quaternionf()

        for (var5 in 0 until var1) {
            stepJacobi(var0, var3, var4, var2)
        }

        var2.normalize()
        return var2
    }

    private fun similarityTransform(var0: Matrix3f, var1: Matrix3f) {
        var0.mul(var1)
        var1.transpose()
        var1.mul(var0)
        var0.set(var1)
    }
    private fun approxGivensQuat(var0: Float, var1: Float, var2: Float): GivensParameters {
        val var3 = 2.0f * (var0 - var2)
        return if (G * var1 * var1 < var3 * var3) GivensParameters.fromUnnormalized(
            var1,
            var3
        ) else PI_4
    }

    fun svdDecompose(var0: Matrix3f): Triple<Quaternionf, Vector3f, Quaternionf> {
        val var1 = Matrix3f(var0)
        var1.transpose()
        var1.mul(var0)
        val var2 = eigenvalueJacobi(var1, 5)
        val var3 = var1.m00
        val var4 = var1.m11
        val var5 = var3.toDouble() < 1.0E-6
        val var6 = var4.toDouble() < 1.0E-6
        var var7: Matrix3f? = var1
        val var8 = var0.rotate(var2)
        val var9 = Quaternionf()
        val var10 = Quaternionf()
        var var11: GivensParameters = if (var5) {
            qrGivensQuat(var8.m11, -var8.m10)
        } else {
            qrGivensQuat(var8.m00, var8.m01)
        }

        val var12 = var11.aroundZ(var10)
        val var13 = var11.aroundZ(var7!!)
        var9.mul(var12)
        var13.transpose().mul(var8)
        var7 = var8
        var11 = if (var5) {
            qrGivensQuat(var13.m22, -var13.m20)
        } else {
            qrGivensQuat(var13.m00, var13.m02)
        }

        var11 = var11.inverse()
        val var14 = var11.aroundY(var10)
        val var15 = var11.aroundY(var7)
        var9.mul(var14)
        var15.transpose().mul(var13)
        var7 = var13
        var11 = if (var6) {
            qrGivensQuat(var15.m22, -var15.m21)
        } else {
            qrGivensQuat(var15.m11, var15.m12)
        }

        val var16 = var11.aroundX(var10)
        val var17 = var11.aroundX(var7)
        var9.mul(var16)
        var17.transpose().mul(var15)
        val var18 = Vector3f(var17.m00, var17.m11, var17.m22)
        return Triple(var9, var18, var2.conjugate())
    }

    private fun qrGivensQuat(var0: Float, var1: Float): GivensParameters {
        val var2 = hypot(var0.toDouble(), var1.toDouble()).toFloat()
        var var3 = if (var2 > 1.0E-6f) var1 else 0.0f
        var var4 = Math.abs(var0) + Math.max(var2, 1.0E-6f)
        if (var0 < 0.0f) {
            val var5 = var3
            var3 = var4
            var4 = var5
        }

        return GivensParameters.fromUnnormalized(var3, var4)
    }

}