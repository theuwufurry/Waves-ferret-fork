package gg.aquatic.waves.util.action.impl.discord

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.runAsync
import me.micartey.webhookly.DiscordWebhook
import me.micartey.webhookly.embeds.EmbedObject
import org.bukkit.entity.Player

class DiscordWebhookAction : AbstractAction<Player>() {

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String) {
        val url = args.string("url") { str -> textUpdater(binder, str) } ?: return
        val content = args.string("content") { str -> textUpdater(binder, str) } ?: return
        val username = args.string("username") { str -> textUpdater(binder, str) } ?: return
        val avatarUrl = args.string("avatar-url") { str -> textUpdater(binder, str) } ?: return
        val tts = args.boolean("tts") { str -> textUpdater(binder, str) } ?: return
        val embeds = args.typed<ArrayList<EmbedObject>>("embeds") { str -> textUpdater(binder, str) } ?: return

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
        PrimitiveObjectArgument("avatar-url", "", false),
        PrimitiveObjectArgument("tts", false, required = false),
        DiscordEmbedArgument("embeds", ArrayList(), false)
    )
}