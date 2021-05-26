package me.tropicalshadow.aurorarpg

import me.tropicalshadow.aurorarpg.commands.AddHealthCommand
import me.tropicalshadow.aurorarpg.commands.AdminItemsCommands
import me.tropicalshadow.aurorarpg.commands.AuroraRPGCommand
import me.tropicalshadow.aurorarpg.commands.SpawnCustomMobCommand
import me.tropicalshadow.aurorarpg.custommobs.CustomMob
import me.tropicalshadow.aurorarpg.database.connection.hikari.MariaDbConnectionFactory
import me.tropicalshadow.aurorarpg.database.connection.utils.StorageCredentials
import me.tropicalshadow.aurorarpg.enums.MESSAGE
import me.tropicalshadow.aurorarpg.listeners.*
import me.tropicalshadow.aurorarpg.mana.ManaManager
import me.tropicalshadow.aurorarpg.titlebar.TitleBarManager
import me.tropicalshadow.aurorarpg.utils.FileHandler
import me.tropicalshadow.aurorarpg.utils.PlayerDataService
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

import org.bukkit.configuration.file.YamlConfiguration


class AuroraRPG : JavaPlugin() {

    lateinit var messageConfig: FileHandler
    lateinit var databaseConfig: FileHandler
    lateinit var database: MariaDbConnectionFactory
    lateinit var playerDataService: PlayerDataService

    override fun onEnable() {
        plugin = this
        ManaManager.MANA = LinkedHashMap()

        messageConfig = FileHandler("messages.yml")
        setupMessageConfig()

        databaseConfig = FileHandler("database.yml")
        setupDatabase()

        TitleBarManager()

        setupListeners()

        setupCommands()

        CustomMob.startUpdateScheduler()

        justIncaseReload()
        logger.info("Plugin Enabled")
    }

    override fun onDisable() {
        logger.info("Plugin Disabled")
    }

    fun justIncaseReload(){
        for (player in Bukkit.getOnlinePlayers()) {
            val data = playerDataService.find(player.uniqueId)
            ManaManager.MANA[data.uniqueId] = data.jsonData.maxMana
            player.inventory.setItem(8,MenuItemListener.MENU_ITEM)
        }
    }

    fun getDatabaseConfig(c: YamlConfiguration) : StorageCredentials {
        val maxPoolSize: Int =
            c.getInt("data.pool-settings.maximum-pool-size", c.getInt("data.pool-size", 10))
        val minIdle: Int = c.getInt("data.pool-settings.minimum-idle", maxPoolSize)
        val maxLifetime: Int = c.getInt("data.pool-settings.maximum-lifetime", 1800000)
        val keepAliveTime: Int = c.getInt("data.pool-settings.keepalive-time", 0)
        val connectionTimeout: Int = c.getInt("data.pool-settings.connection-timeout", 5000)
        if(!c.isConfigurationSection("data.pool-settings.properties")){
            c.createSection("data.pool-settings.properties")
        }
        val poolSettingsProperties = c.getConfigurationSection("data.pool-settings.properties")
        val props: Map<String, String> = poolSettingsProperties?.getValues(false) as Map<String, String>
        val address: String = c.getString("data.address", "")!!
        val database = c.getString("data.database", "")!!
        val username = c.getString("data.username", "")!!
        val password = c.getString("data.password","")!!
        return StorageCredentials(address,database,username,password,maxPoolSize,
            minIdle, maxLifetime, keepAliveTime, connectionTimeout, props)
    }
    fun setupDatabase(){
        databaseConfig.reload()
        val creds: StorageCredentials = getDatabaseConfig(databaseConfig.config)
        database = MariaDbConnectionFactory(creds)
        logger.info("Database Init...")
        database.init(this)
        try{
            if(database.connection == null){
                logger.severe("DATABASE FAILED TO CONNECT, Check config!!!")
                server.pluginManager.disablePlugin(this)
                server.shutdown()
            }
        }catch (e: Exception){
            logger.severe("DATABASE FAILED TO CONNECT, Check config!!!")
            server.pluginManager.disablePlugin(this)
            server.shutdown()
        }
        playerDataService = PlayerDataService(database)

    }

    private fun setupMessageConfig(){
        MESSAGE.values().forEach {
            if(!messageConfig.config.isString(it.path))messageConfig.config.set(it.path,it.defaultVal)
        }
        messageConfig.save()
    }

    private fun setupListeners(){
        OnEntityDamageListener()
        OnEntityDeathListener()
        MenuItemListener()
        OnLeaveListener()
        OnJoinListener()
    }

    private fun setupCommands(){
        SpawnCustomMobCommand()
        AdminItemsCommands()
        AuroraRPGCommand()
        AddHealthCommand()
    }


    companion object {
        @JvmStatic lateinit var plugin: AuroraRPG

        fun colourise(input: String): String {
            return ChatColor.translateAlternateColorCodes('&', input)
        }

    }

}