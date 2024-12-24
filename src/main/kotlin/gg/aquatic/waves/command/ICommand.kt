package gg.aquatic.waves.command

import org.bukkit.command.CommandSender

interface ICommand {

    fun run(sender: CommandSender, args: Array<out String>)
    fun tabComplete(sender: CommandSender, args: Array<out String>): List<String>

}