package me.tropicalshadow.aurorarpg.titlebar

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.dataclasses.PlayerData
import me.tropicalshadow.aurorarpg.mana.ManaManager
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class TitleBarManager {


    init{
        val plugin = AuroraRPG.plugin
        val task: BukkitTask = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            plugin.server.onlinePlayers.forEach { player ->
                val playerData = PlayerDataService.CACHE[player.uniqueId] ?: return@forEach
                // TODO - plugin.server.scheduler.runTask(plugin, getCalculator(mana)) increment MANA
                sendTitle(player, playerData)
            }
         }, 20.toLong() * 10, 20.toLong() * 2)
    }


    companion object{

        fun sendTitle(player: Player,playerData: PlayerData){
            var format = "{health} {mana} {username}"
            val attr = playerData.jsonData
            format = format.replace("{health}", player.health.toInt().toString() +"/"+ attr.maxHealth.toInt().toString())
                .replace("{mana}", ManaManager.MANA[player.uniqueId].toString() +"/"+attr.maxMana.toString())
                .replace("{username", player.name)
            player.sendActionBar( Component.text(format,NamedTextColor.AQUA))
        }
    }
}