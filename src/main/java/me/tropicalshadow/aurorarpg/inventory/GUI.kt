package me.tropicalshadow.aurorarpg.inventory

import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.listeners.GuiListener
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import java.util.WeakHashMap
import org.bukkit.event.inventory.InventoryEvent







open class GUI(private val inventoryName: String, private val rows: Int) : InventoryHolder {

    init{
        if(listener == null){
            listener = GuiListener()
        }
        inventory
        addInventory(inventory,this)
    }

    var inv: Inventory? = null

    var canClick = false

    var onClick: Consumer<InventoryClickEvent>? = null
    var onClose: Consumer<InventoryCloseEvent>? = null

    open fun callOnClick(event: InventoryClickEvent?) {
        callCallback(onClick, event!!, "onClick")
    }
    open fun callOnClose(event: InventoryCloseEvent?) {
        callCallback(onClose, event!!, "onClose")
    }
    open fun <T : InventoryEvent> callCallback(callback: Consumer<T>?, event: T, callbackName: String) {
        if (callback == null) return
        try {
            callback.accept(event)
        } catch (t: Throwable) {
            var message: String? = "Exception while handling $callbackName"
            if (event is InventoryClickEvent) {
                message += ", slot=" + event.slot
            }
            message += t.message
            AuroraRPG.plugin.logger.severe(message)
        }
    }

    fun fillInventoryWith(item: ItemStack){
        inventory.clear()
        for (i in 0 until inventory.size){
            inventory.setItem(i,item)
        }
    }
    fun setItem(index:Int, item: ItemStack){
        inventory.setItem(index,item)
    }


    override fun getInventory(): Inventory {
        if(inv == null){
            inv = Bukkit.createInventory(this,rows*9, Component.text(AuroraRPG.colourise(inventoryName)))
        }
        return inv!!
    }

    companion object{
        var listener: GuiListener? = null
        private val GUI_INVENTORIES: WeakHashMap<Inventory, GUI> = WeakHashMap()

        protected fun addInventory(key: Inventory, value: GUI) {
            GUI_INVENTORIES[key] = value
        }


        fun getGui(inv:Inventory) : GUI? {
            return GUI_INVENTORIES[inv]
        }

    }

}