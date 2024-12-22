package gg.aquatic.waves.util.action.impl.discord

import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.getSectionList
import me.micartey.webhookly.embeds.*
import org.bukkit.configuration.ConfigurationSection
import java.awt.Color

class DiscordEmbedArgument(id: String, defaultValue: List<EmbedObject>?, required: Boolean) :
    AquaticObjectArgument<List<EmbedObject>>(
        id,
        defaultValue, required
    ) {
    override val serializer: AbstractObjectArgumentSerializer<List<EmbedObject>?> = Companion

    override fun load(section: ConfigurationSection): List<EmbedObject>? {
        return serializer.load(section, id) ?: return defaultValue
    }

    companion object : AbstractObjectArgumentSerializer<List<EmbedObject>?>() {
        override fun load(section: ConfigurationSection, id: String): List<EmbedObject> {
            val list = mutableListOf<EmbedObject>()

            for (embedSection in section.getSectionList(id)) {
                val embed = EmbedObject()
                if (embedSection.contains("title")) {
                    embed.title = embedSection.getString("title")
                }
                if (embedSection.contains("description")) {
                    embed.description = embedSection.getString("description")
                }
                if (embedSection.contains("url")) {
                    embed.url = embedSection.getString("url")
                }
                if (embedSection.contains("author")) {
                    val authorName = embedSection.getString("author.name")
                    val authorUrl = embedSection.getString("author.url")
                    val authorIconUrl = embedSection.getString("author.icon-url")
                    embed.author = Author(
                        authorName,
                        authorUrl,
                        authorIconUrl
                    )
                }
                if (embedSection.contains("footer")) {
                    val footer = Footer(
                        embedSection.getString("footer.text"),
                        embedSection.getString("footer.icon-url")
                    )
                    embed.footer = footer
                }
                if (embedSection.contains("color")) {
                    val color = Color(
                        embedSection.getInt("color.red"),
                        embedSection.getInt("color.green"),
                        embedSection.getInt("color.blue")
                    )
                    embed.color = color
                }
                if (embedSection.contains("image")) {
                    val imageUrl = embedSection.getString("image")
                    embed.image = Image(imageUrl)
                }
                if (embedSection.contains("thumbnail")) {
                    val thumbnailUrl = embedSection.getString("thumbnail")
                    embed.thumbnail = Thumbnail(thumbnailUrl)
                }
                list += embed
            }
            return list
        }
    }
}