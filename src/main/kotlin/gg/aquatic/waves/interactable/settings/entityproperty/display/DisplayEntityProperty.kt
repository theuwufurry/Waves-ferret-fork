package gg.aquatic.waves.interactable.settings.entityproperty.display

import gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.packetevents.type.DisplayEntityDataBuilder
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f

interface DisplayEntityProperty : EntityProperty {

    class Billboard(private val billboard: Display.Billboard) : DisplayEntityProperty {
        override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
            if (builder is DisplayEntityDataBuilder) {
                builder.setBillboard(billboard)
            }
        }


        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val billboard = Display.Billboard.valueOf(section.getString("billboard", "NONE")!!)
                return Billboard(billboard)
            }
        }

    }

    class InterpolationDelay(private val delay: Int) : DisplayEntityProperty {
        override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
            if (builder is DisplayEntityDataBuilder) {
                builder.setInterpolationDelay(delay)
            }
        }
        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val delay = section.getInt("interpolation-delay", 0)
                return InterpolationDelay(delay)
            }
        }
    }
    class InterpolationDuration(private val duration: Int) : DisplayEntityProperty {
        override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
            if (builder is DisplayEntityDataBuilder) {
                builder.setTransformationInterpolationDuration(duration)
            }
        }
        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val duration = section.getInt("interpolation-duration", 0)
                return InterpolationDuration(duration)
            }
        }
    }
    class TeleportInterpolationDuration(private val duration: Int) : DisplayEntityProperty {
        override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
            if (builder is DisplayEntityDataBuilder) {
                builder.setPosRotInterpolationDuration(duration)
            }
        }
        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val duration = section.getInt("teleport-interpolation-duration", 0)
                return TeleportInterpolationDuration(duration)
            }
        }
    }

    class Transformation(private val transformation: org.bukkit.util.Transformation): DisplayEntityProperty {

        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val s = section.getConfigurationSection("transformation") ?: return Transformation(
                    Transformation(
                        Vector3f(),
                        AxisAngle4f(),
                        Vector3f(),
                        AxisAngle4f()
                    )
                )

                val scaleStr = s.getString("scale")
                val scale = if (scaleStr != null) {
                    val split = scaleStr.split(";")
                    Vector3f(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
                } else Vector3f(1f, 1f, 1f)

                val translationStr = s.getString("translation")
                val translation = if (translationStr != null) {
                    val split = translationStr.split(";")
                    Vector3f(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
                }
                else Vector3f(0f, 0f, 0f)

                val rotationStr = s.getString("rotation")
                val rotation = if (rotationStr != null) {
                    val split = rotationStr.split(";")
                    if (split.size > 3) {
                        Quaternionf(split[0].toFloat(), split[1].toFloat(), split[2].toFloat(), split[3].toFloat())
                    }
                    else Quaternionf().rotationXYZ(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
                } else Quaternionf()

                return Transformation(Transformation(translation, rotation, scale, Quaternionf()))
            }
        }

        override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
            if (builder is DisplayEntityDataBuilder) {
                builder.setTransformation(transformation)
            }
        }
    }

}