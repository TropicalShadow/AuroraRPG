package me.tropicalshadow.aurorarpg.inventory

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.utils.ItemBuilder
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.function.Consumer

class MenuGui(val player: Player) : GUI("The Menu Of Menu's",5) {

    var playerSkullItem: ItemStack

    init{
        val playerData = PlayerDataService.CACHE[player.uniqueId]?: throw Exception("Player data not found and it should be found so find it")
        fillInventoryWith(ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").build())
        playerSkullItem = ItemBuilder().setMaterial(Material.PLAYER_HEAD).setName(player.name).build()
        val headMeta: SkullMeta = playerSkullItem.itemMeta as SkullMeta
        headMeta.owningPlayer = player
        headMeta.playerProfile = player.playerProfile
        val health = playerData.jsonData.maxHealth.toInt()
        val mana = playerData.jsonData.maxMana
        headMeta.lore(listOf(Component.text(AuroraRPG.colourise("&cHealth: $health")), Component.text(AuroraRPG.colourise("&bMana: $mana"))))
        playerSkullItem.itemMeta = headMeta

        setItem(13,playerSkullItem)

        onClick = Consumer {

            it.isCancelled = true
        }
    }

    fun show(){
        player.openInventory(inventory)
    }


}