package me.tropicalshadow.aurorarpg.commands

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.commands.annotations.ShadowCommandInfo
import org.bukkit.command.Command

import org.bukkit.entity.Player

import org.bukkit.command.CommandSender

import org.bukkit.command.TabExecutor
import org.jetbrains.annotations.Nullable
import java.util.*


open class ShadowCommand : TabExecutor {
    val commandInfo: ShadowCommandInfo = javaClass.getDeclaredAnnotation(ShadowCommandInfo::class.java)

    init {
        Objects.requireNonNull(commandInfo, "Commands must have ShadowCommandInfo Annotation")
        val cmd = AuroraRPG.plugin.getCommand(commandInfo.name) ?: throw Error("Command not in config")
        cmd.setExecutor(this)
        cmd.setTabCompleter(this)
        cmd.aliases = commandInfo.aliases.toMutableList()
        cmd.description = commandInfo.description
        cmd.description = commandInfo.usage.replace("<command>",commandInfo.name)
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val isPlayer = sender is Player
        if (commandInfo.permission.isNotEmpty()) {
            if (!sender.hasPermission(commandInfo.permission)) {
                sender.sendMessage(commandInfo.permissionErr)
                return true
            }
        }
        if (commandInfo.isPlayerOnly) {
            if (!isPlayer) {
                sender.sendMessage(commandInfo.isPlayerOnlyErr)
                return true
            }
            execute(sender as Player, args)
            return true
        }
        execute(sender, args)
        return true
    }

    @Nullable
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val isPlayer = sender is Player
        if (commandInfo.permission.isNotEmpty()) {
            if (!sender.hasPermission(commandInfo.permission)) {
                return ArrayList()
            }
        }
        return if (commandInfo.isPlayerOnly) {
            if (!isPlayer) {
                ArrayList()
            } else tabComplete(sender as Player, args)
        } else tabComplete(sender, args)
    }

    open fun execute(player: Player, args: Array<String>){}
    open fun execute(sender: CommandSender, args: Array<String>){}
    open fun tabComplete(sender: CommandSender, args: Array<String>): ArrayList<String> = ArrayList()


    open fun tabComplete(player: Player, args: Array<String>): ArrayList<String> = ArrayList()


}