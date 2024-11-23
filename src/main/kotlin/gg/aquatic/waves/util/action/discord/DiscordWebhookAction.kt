package gg.aquatic.waves.util.action.discord

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.aquaticseries.lib.util.runAsync
import me.micartey.webhookly.DiscordWebhook
import me.micartey.webhookly.embeds.EmbedObject
import org.bukkit.entity.Player
import java.util.function.BiFunction

class DiscordWebhookAction : AbstractAction<Player>() {
    @Suppress("UNCHECKED_CAST")
    override fun run(binder: Player, args: Map<String, Any?>, textUpdater: BiFunction<Player, String, String>) {
        val url = args["url"] as String
        val content = args["content"] as String
        val username = args["username"] as String
        val avatarUrl = args["avatar_url"] as String
        val tts = args["tts"] as Boolean
        val embeds = args["embeds"] as ArrayList<EmbedObject>

        runAsync {
            val webhook = DiscordWebhook(url)
            webhook.setContent(content)
            webhook.setAvatarUrl(avatarUrl)
            webhook.setUsername(username)
            webhook.setTts(tts)
            webhook.embeds += embeds

            webhook.execute()
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("url", "", true),
            PrimitiveObjectArgument("content", null, false),
            PrimitiveObjectArgument("username", "AquaticCrates", false),
            PrimitiveObjectArgument("avatar_url", "", false),
            PrimitiveObjectArgument("tts", false, required = false),
            DiscordEmbedArgument("embeds", ArrayList(), false)
        )
    }
}