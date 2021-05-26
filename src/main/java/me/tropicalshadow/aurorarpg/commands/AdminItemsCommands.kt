package me.tropicalshadow.aurorarpg.commands

import me.tropicalshadow.aurorarpg.commands.annotations.ShadowCommandInfo
import me.tropicalshadow.aurorarpg.listeners.MenuItemListener
import org.bukkit.entity.Player

@ShadowCommandInfo("adminitems",isPlayerOnly = true)
class AdminItemsCommands : ShadowCommand() {

    override fun execute(player: Player, args: Array<String>) {
        player.inventory.addItem(MenuItemListener.MENU_ITEM)
    }
}