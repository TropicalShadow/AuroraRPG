package me.tropicalshadow.aurorarpg.database.connection.hikari

import org.bukkit.plugin.PluginLogger

import com.google.common.collect.ImmutableList


import net.kyori.adventure.text.format.NamedTextColor

import java.sql.SQLException

import java.util.LinkedHashMap

import com.zaxxer.hikari.HikariDataSource

import com.zaxxer.hikari.HikariConfig
import me.tropicalshadow.aurorarpg.AuroraRPG

import java.util.concurrent.TimeUnit

import me.tropicalshadow.aurorarpg.database.connection.ConnectionFactory
import me.tropicalshadow.aurorarpg.database.connection.utils.StorageCredentials
import net.kyori.adventure.text.Component
import java.lang.Exception
import java.sql.Connection
import java.util.logging.Logger


abstract class HikariConnectionFactory(private val configuration: StorageCredentials) : ConnectionFactory {
    private var hikari: HikariDataSource? = null

    /**
     * Gets the default port used by the database
     *
     * @return the default port
     */
    protected abstract fun defaultPort(): String?

    /**
     * Configures the [HikariConfig] with the relevant database properties.
     *
     *
     * Each driver does this slightly differently...
     *
     * @param config the hikari config
     * @param address the database address
     * @param port the database port
     * @param databaseName the database name
     * @param username the database username
     * @param password the database password
     */
    protected abstract fun configureDatabase(
        config: HikariConfig?,
        address: String?,
        port: String?,
        databaseName: String?,
        username: String?,
        password: String?
    )

    /**
     * Allows the connection factory instance to override certain properties before they are set.
     *
     * @param properties the current properties
     */
    protected open fun overrideProperties(properties: MutableMap<String?, String?>) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", TimeUnit.SECONDS.toMillis(30).toString())
    }

    /**
     * Sets the given connection properties onto the config.
     *
     * @param config the hikari config
     * @param properties the properties
     */
    protected open fun setProperties(config: HikariConfig, properties: Map<String?, String?>) {
        for ((key, value) in properties) {
            config.addDataSourceProperty(key, value)
        }
    }

    /**
     * Called after the Hikari pool has been initialised
     */
    protected open fun postInitialize() {}
    override fun init(plugin: AuroraRPG) {
        val config: HikariConfig
        config = try {
            HikariConfig()
        } catch (e: LinkageError) {
            handleClassloadingError(e, plugin)
            throw e
        }

        // set pool name so the logging output can be linked back to us
        config.poolName = "aurorarpg-hikari"

        // get the database info/credentials from the config file
        val addressSplit: List<String> = configuration.getAddress().split(":")
        val address = addressSplit[0]
        val port = if (addressSplit.size > 1) addressSplit[1] else defaultPort()!!

        // allow the implementation to configure the HikariConfig appropriately with these values
        try {
            configureDatabase(
                config,
                address,
                port,
                configuration.getDatabase(),
                configuration.getUsername(),
                configuration.getPassword()
            )
        } catch (e: NoSuchMethodError) {
            handleClassloadingError(e, plugin)
        }

        // get the extra connection properties from the config
        val properties: MutableMap<String?, String?> = configuration.properties.toMutableMap()

        // allow the implementation to override/make changes to these properties
        overrideProperties(properties)

        // set the properties
        setProperties(config, properties)

        // configure the connection pool
        config.maximumPoolSize = configuration.maxPoolSize
        config.minimumIdle = configuration.minIdleConnections
        config.maxLifetime = configuration.maxLifetime.toLong()
        config.keepaliveTime = configuration.keepAliveTime.toLong()
        config.connectionTimeout = configuration.connectionTimeout.toLong()

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.initializationFailTimeout = -1
        hikari = HikariDataSource(config)
        postInitialize()
    }

    override fun shutdown() {
        if (hikari != null) {
            hikari!!.close()
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection?
        get() {
            if (hikari == null) {
                throw SQLException("Unable to get a connection from the pool. (hikari is null)")
            }
            return hikari!!.connection
                ?: throw SQLException("Unable to get a connection from the pool. (getConnection returned null)")
        }
    override val meta: Map<Any, Any>
        get() {
            val meta: MutableMap<Component, Component> = LinkedHashMap<Component, Component>()
            var success = true
            val start = System.currentTimeMillis()
            try {
                connection.use { c -> c?.createStatement().use { s -> s?.execute("/* ping */ SELECT 1") } }
            } catch (e: SQLException) {
                success = false
            }
            if (success) {
                val duration = System.currentTimeMillis() - start
                meta[Component.translatable("aurorarpg.info.storage.meta.ping-key")] =
                    Component.text(duration.toString() + "ms", NamedTextColor.GREEN)
            }
            meta[Component.translatable("aurorarpg.info.storage.meta.connected-key")] = Component.text(success.toString())
            return meta.toMap()
        }

    companion object {
        // dumb plugins seem to keep doing stupid stuff with shading of SLF4J and Log4J.
        // detect this and print a more useful error message.
        private fun handleClassloadingError(throwable: Throwable, plugin: AuroraRPG) {
            val noteworthyClasses: List<String> = ImmutableList.of(
                "org.slf4j.LoggerFactory",
                "org.slf4j.ILoggerFactory",
                "org.apache.logging.slf4j.Log4jLoggerFactory",
                "org.apache.logging.log4j.spi.LoggerContext",
                "org.apache.logging.log4j.spi.AbstractLoggerAdapter",
                "org.slf4j.impl.StaticLoggerBinder",
                "org.slf4j.helpers.MessageFormatter"
            )
            val logger: Logger = plugin.getLogger()
            logger.warning("A " + throwable.javaClass.simpleName + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.")
            logger.warning("Please check for other plugins below (and try loading LuckPerms without them installed) before reporting the issue.")
            for (className in noteworthyClasses) {
                var clazz: Class<*> = try {
                    Class.forName(className)
                } catch (e: Exception) {
                    continue
                }
                val loader = clazz.classLoader
                var loaderName: String = loader.toString()
                logger.warning("Class $className has been loaded by: $loaderName")
            }
        }
    }

}