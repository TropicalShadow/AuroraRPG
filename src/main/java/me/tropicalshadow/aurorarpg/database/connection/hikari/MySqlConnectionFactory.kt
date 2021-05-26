package me.tropicalshadow.aurorarpg.database.connection.hikari

import java.sql.SQLException

import java.sql.DriverManager

import java.util.function.Function;

import com.zaxxer.hikari.HikariConfig

import me.tropicalshadow.aurorarpg.database.connection.utils.StorageCredentials


class MySqlConnectionFactory(configuration: StorageCredentials?) : HikariConnectionFactory(configuration!!) {
    override val implementationName: String
        get() = "MySQL"

    override fun defaultPort(): String {
        return "3306"
    }

    override fun configureDatabase(
        config: HikariConfig?,
        address: String?,
        port: String?,
        databaseName: String?,
        username: String?,
        password: String?
    ) {
        config!!.driverClassName = "com.mysql.cj.jdbc.Driver"
        config.jdbcUrl = "jdbc:mysql://$address:$port/$databaseName"
        config.username = username
        config.password = password
    }

    override fun postInitialize() {
        super.postInitialize()

        // Calling Class.forName("com.mysql.cj.jdbc.Driver") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        val drivers = DriverManager.getDrivers()
        while (drivers.hasMoreElements()) {
            val driver = drivers.nextElement()
            if (driver.javaClass.name == "com.mysql.cj.jdbc.Driver") {
                try {
                    DriverManager.deregisterDriver(driver)
                } catch (e: SQLException) {
                    // ignore
                }
            }
        }
    }

    override fun overrideProperties(properties: MutableMap<String?, String?>) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true")
        properties.putIfAbsent("prepStmtCacheSize", "250")
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048")
        properties.putIfAbsent("useServerPrepStmts", "true")
        properties.putIfAbsent("useLocalSessionState", "true")
        properties.putIfAbsent("rewriteBatchedStatements", "true")
        properties.putIfAbsent("cacheResultSetMetadata", "true")
        properties.putIfAbsent("cacheServerConfiguration", "true")
        properties.putIfAbsent("elideSetAutoCommits", "true")
        properties.putIfAbsent("maintainTimeStats", "false")
        properties.putIfAbsent("alwaysSendSetIsolation", "false")
        properties.putIfAbsent("cacheCallableStmts", "true")

        // https://stackoverflow.com/a/54256150
        // It's not super important which timezone we pick, because we don't use time-based
        // data types in any of our schemas/queries.
        properties.putIfAbsent("serverTimezone", "UTC")
        super.overrideProperties(properties)
    }

    // use backticks for quotes
    override val statementProcessor: Function<String, String>
        get() = Function<String, String> { s -> s.replace('\'', '`') } // use backticks for quotes
}