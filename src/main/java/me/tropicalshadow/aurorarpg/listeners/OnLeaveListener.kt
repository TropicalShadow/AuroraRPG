package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.mana.ManaManager
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class OnLeaveListener : ShadowListener() {

    @EventHandler()
    fun onLeave(event: PlayerQuitEvent){
        AuroraRPG.plugin.playerDataService.removeFromCache(event.player.uniqueId)
        ManaManager.MANA.remove(event.player.uniqueId)
    }
}