package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.mana.ManaManager
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

class OnJoinListener : ShadowListener() {

    @EventHandler()
    fun onLogin(event: PlayerLoginEvent){
        AuroraRPG.plugin.playerDataService.find(event.player.uniqueId)

    }
    @EventHandler()
    fun onJoin(event: PlayerJoinEvent){
        val playerData = PlayerDataService.CACHE[event.player.uniqueId] ?: return
        val attributeInstance = event.player.getAttribute( Attribute.GENERIC_MAX_HEALTH)
        attributeInstance!!.baseValue = playerData.jsonData.maxHealth
        event.player.health = attributeInstance.baseValue
        ManaManager.MANA[event.player.uniqueId] = playerData.jsonData.maxMana
        event.player.inventory.setItem(8,MenuItemListener.MENU_ITEM)
    }
}