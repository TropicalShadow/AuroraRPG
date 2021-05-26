package me.tropicalshadow.aurorarpg.utils

import me.tropicalshadow.aurorarpg.AuroraRPG
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemBuilder {

    var displayName = ""
    var mat = Material.AIR
    var lore = arrayOf("")

    fun setName(name: String) : ItemBuilder{
        displayName = name
        return this
    }
    fun setMaterial(material: Material) : ItemBuilder{
        mat = material
        return this

    }
    fun lore(lore: Array<String>) : ItemBuilder{
        this.lore = lore
        return this
    }
    fun build() : ItemStack{
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text(AuroraRPG.colourise(displayName)))
        val compLore:List<Component> = lore.map { Component.text(AuroraRPG.colourise(it)) }
        meta.lore(compLore)
        item.itemMeta = meta
        return item
    }



}