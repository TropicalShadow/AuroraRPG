package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.custommobs.CustomMob
import me.tropicalshadow.aurorarpg.titlebar.TitleBarManager
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class OnEntityDamageListener : ShadowListener() {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent){
        Bukkit.getScheduler().runTaskAsynchronously(AuroraRPG.plugin, Runnable{
            if(event.entityType != EntityType.PLAYER){
                val mob = CustomMob.CREATURES[event.entity.uniqueId] ?: return@Runnable
                mob.update()
            }else{
                val playerData = PlayerDataService.CACHE[event.entity.uniqueId] ?: return@Runnable
                TitleBarManager.sendTitle(event.entity as Player,playerData)
            }
        })
    }

}