package me.tropicalshadow.aurorarpg.enums

import me.tropicalshadow.aurorarpg.AuroraRPG
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.jetbrains.annotations.Nullable

enum class MESSAGE(val defaultVal: String, val path: String) {

    MESSAGE_FORMAT("Default Command", "debug.command");

    private fun getMessage(): @Nullable String {
        return AuroraRPG.colourise(AuroraRPG.plugin.messageConfig.config.getString(path,defaultVal)!!)
    }

    fun sendMessage(sender: CommandSender, vararg args: Any?){
        var message = getMessage()

        if(args.isNotEmpty() && message.contains("%s")) {
            message = message.format(args)
        }
        sender.sendMessage(Component.text(AuroraRPG.colourise(message)))
    }

}