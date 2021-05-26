package me.tropicalshadow.aurorarpg.commands

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.commands.annotations.ShadowCommandInfo
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ShadowCommandInfo("addhealth", isPlayerOnly = true)
class AddHealthCommand : ShadowCommand() {

    override fun execute(player: Player, args: Array<String>) {
        var playerData = PlayerDataService.CACHE[player.uniqueId]
        if(playerData == null){
            playerData = AuroraRPG.plugin.playerDataService.find(player.uniqueId)
        }
        playerData.jsonData.maxHealth += 100
        PlayerDataService.CACHE[player.uniqueId] = playerData
        AuroraRPG.plugin.playerDataService.update(player.uniqueId)
        val attributeInstance = player.getAttribute( Attribute.GENERIC_MAX_HEALTH)
        attributeInstance!!.baseValue = playerData.jsonData.maxHealth
        player.health = playerData.jsonData.maxHealth
        player.sendMessage("Done!")
    }

}