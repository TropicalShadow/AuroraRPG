package me.tropicalshadow.aurorarpg.custommobs

import org.bukkit.entity.EntityType
import org.bukkit.loot.LootTable

enum class MOB(var customName: String,
               var displayname: String,
               var type: EntityType,
               var health: Double,
               var level: Int,
               var ai: Boolean,
               var loot: LootTable?
               ){//var customMob: CustomMob) {
    UNKNOWN("unknown","Unknown", EntityType.VEX,20.0,10,false,null),
    FERAL_GHOUL("ghoul","Feral Ghoul", EntityType.ZOMBIE, 5.0, 2, true, null);

    fun mob():CustomMob{
        return  CustomMob(customName, displayname,type, health, level, ai,loot)
    }

    companion object{
        fun fromName(name:String) : MOB{
            values().forEach { if(it.customName.equals(name,true))return it }
            return UNKNOWN
        }
    }

}