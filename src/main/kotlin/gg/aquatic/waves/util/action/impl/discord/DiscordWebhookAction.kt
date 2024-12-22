package gg.aquatic.waves.util.action.impl.discord

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.runAsync
import me.micartey.webhookly.DiscordWebhook
import me.micartey.webhookly.embeds.EmbedObject
import org.bukkit.entity.Player

class DiscordWebhookAction : AbstractAction<Player>() {

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
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

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("url", "", true),
        PrimitiveObjectArgument("content", null, false),
        PrimitiveObjectArgument("username", "AquaticCrates", false),
        PrimitiveObjectArgument("avatar_url", "", false),
        PrimitiveObjectArgument("tts", false, required = false),
        DiscordEmbedArgument("embeds", ArrayList(), false)
    )
}