package me.tropicalshadow.aurorarpg.commands

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.commands.annotations.ShadowCommandInfo
import me.tropicalshadow.aurorarpg.enums.MESSAGE
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ShadowCommandInfo("aurorarpg",description = "plugin root command, mostly for debugging")
class AuroraRPGCommand : ShadowCommand() {

    override fun execute(sender: CommandSender, args: Array<String>){
        MESSAGE.MESSAGE_FORMAT.sendMessage(sender)

        if(sender is Player){
            if(args.isNotEmpty()){
               if(args[0].equals("reset",true)){
                   AuroraRPG.plugin.playerDataService.delete(sender.uniqueId)
                   AuroraRPG.plugin.playerDataService.find(sender.uniqueId)

                   return
               }
            }
            val playerData = PlayerDataService.CACHE[sender.uniqueId]
            sender.sendMessage("data is at "+playerData!!.jsonData.maxHealth.toString())
        }
    }

}