package me.tropicalshadow.aurorarpg.commands

import me.tropicalshadow.aurorarpg.commands.annotations.ShadowCommandInfo
import me.tropicalshadow.aurorarpg.custommobs.MOB
import org.bukkit.entity.Player


@ShadowCommandInfo("spawncustommob", isPlayerOnly = true)
class SpawnCustomMobCommand : ShadowCommand() {

    override fun execute(player: Player, args: Array<String>) {
        var mob: MOB
        if(args.isNotEmpty()){
            val name = args[0]
            mob = MOB.fromName(name)
        }else{
            mob = MOB.UNKNOWN
        }
        mob.mob().summon(player.location)
        player.sendMessage("Spawned Custom Mob")
    }

}