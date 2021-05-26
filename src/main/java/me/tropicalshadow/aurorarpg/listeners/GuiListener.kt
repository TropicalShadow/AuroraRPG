package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.inventory.GUI
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.Bukkit

import org.bukkit.event.inventory.InventoryCloseEvent




class GuiListener : ShadowListener() {

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val gui: GUI = GUI.getGui(event.inventory) ?: return
        if (!gui.canClick) {
            event.isCancelled = true
        }
        event.view.getInventory(event.rawSlot)?: return
        gui.callOnClick(event)
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val gui: GUI = GUI.getGui(event.inventory) ?: return
        Bukkit.getScheduler().runTask(AuroraRPG.plugin, Runnable {
            val humanEntity = event.player
            humanEntity.closeInventory()
        })

        gui.callOnClose(event)
    }
}