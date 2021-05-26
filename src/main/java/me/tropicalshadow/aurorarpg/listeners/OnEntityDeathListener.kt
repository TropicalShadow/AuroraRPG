package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.custommobs.CustomMob
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

class OnEntityDeathListener : ShadowListener() {


    @EventHandler
    fun onEntityDeath(event:EntityDeathEvent){
        if(event.entityType == EntityType.PLAYER){
            val player = event.entity as Player
            player.sendMessage("Ouch that looked like it hurt.")
        }else{
            val entity = event.entity
            val customMob = CustomMob.CREATURES[entity.uniqueId]
            if(customMob != null){
                customMob.death(event)
                CustomMob.CREATURES.remove(entity.uniqueId)
            }
        }
    }
}