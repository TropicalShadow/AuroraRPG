package me.tropicalshadow.aurorarpg.custommobs

import me.tropicalshadow.aurorarpg.AuroraRPG
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTable
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.LinkedHashMap


data class CustomMob(var name: String,
                var displayname: String,
                var type: EntityType,
                var health: Double,
                var level: Int,
                var ai: Boolean,
                var drops: LootTable?
                     ) {
    lateinit var entity: LivingEntity
    fun summon(loc: Location){
        loc.world.spawnEntity(loc,type, CreatureSpawnEvent.SpawnReason.CUSTOM) {
            if(it is LivingEntity){
                entity = it
                entity.isCustomNameVisible = true
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
                entity.health = health
                entity.setAI(ai)
                entity.canPickupItems = false
                if(it is Ageable){
                    it.setAdult()
                }
                entity.equipment!!.clear()
                update()
                CREATURES[it.uniqueId] = this
            }
        }
    }

    fun death(event: EntityDeathEvent){
        //TODO - give xp or something?
        event.drops.clear()
        if (drops != null){
            event.drops.addAll(drops!!.populateLoot(Random(), LootContext.Builder(event.entity.location).build()))
        }
    }

    fun update(){
        if(entity.isDead){
            CREATURES.remove(entity.uniqueId)
            return
        }
        val percentage = (entity.health / health)
        var healthMessage: String
        if(percentage >= 0.5){
            healthMessage = "&a"
        }else if(percentage >= 0.25){
            healthMessage = "&6"
        }else{
            healthMessage = "&c"
        }
        healthMessage += "${entity.health.toInt()}&r/&a${health.toInt()}"
        val format = "&8[&7lvl${level}&8] &r$displayname $healthMessage \u2764"
        entity.customName = AuroraRPG.colourise(format)
    }


    companion object {
        @JvmStatic var CREATURES: LinkedHashMap<UUID, CustomMob> = LinkedHashMap()

        fun startUpdateScheduler(){
            Bukkit.getScheduler().runTaskTimerAsynchronously(AuroraRPG.plugin,Runnable {
                CREATURES.values.forEach {
                    it.update()
                }
            },20.toLong()*1,20.toLong())
        }

    }
}