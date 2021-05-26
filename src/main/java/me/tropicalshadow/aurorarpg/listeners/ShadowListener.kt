package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import org.bukkit.event.Listener

open class ShadowListener : Listener {

    init{
        val pm = AuroraRPG.plugin.server.pluginManager
        pm.registerEvents(this, AuroraRPG.plugin)
    }
}