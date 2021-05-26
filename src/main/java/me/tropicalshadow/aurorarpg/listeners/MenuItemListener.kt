package me.tropicalshadow.aurorarpg.listeners

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.inventory.MenuGui
import me.tropicalshadow.aurorarpg.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class MenuItemListener : ShadowListener() {


    @EventHandler
    fun onItemDrop(event:PlayerDropItemEvent){
        if(event.itemDrop.itemStack.isSimilar(MENU_ITEM)){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onItemMove(event: InventoryMoveItemEvent){
        if(event.item.isSimilar( MENU_ITEM))event.isCancelled = true
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent){
        if(event.player.equipment!!.getItem(event.hand!!).isSimilar(MENU_ITEM)){
            MenuGui(event.player).show()
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onMoveItem(event: InventoryClickEvent){
        if(event.currentItem == null) return
        if(event.currentItem!!.isSimilar(MENU_ITEM)){
            event.isCancelled = true
        }
    }

    companion object{
        val MENU_ITEM = ItemBuilder().setName("&bMenu").setMaterial(Material.COMPASS).build()
    }
}