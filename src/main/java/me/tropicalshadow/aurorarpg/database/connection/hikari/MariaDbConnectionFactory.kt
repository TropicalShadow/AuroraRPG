package me.tropicalshadow.aurorarpg.database.connection.hikari

import java.util.stream.Collectors

import java.util.function.Function;

import com.zaxxer.hikari.HikariConfig

import me.tropicalshadow.aurorarpg.database.connection.utils.StorageCredentials


class MariaDbConnectionFactory(configuration: StorageCredentials?) : HikariConnectionFactory(configuration!!) {
    override val implementationName: String
        get() = "MariaDB"

    override fun defaultPort(): String? {
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
        config!!.dataSourceClassName = "org.mariadb.jdbc.MariaDbDataSource"
        config.addDataSourceProperty("serverName", address)
        config.addDataSourceProperty("port", port)
        config.addDataSourceProperty("databaseName", databaseName)
        config.username = username
        config.password = password
    }

    override fun setProperties(config: HikariConfig, properties: Map<String?, String?>) {
        val propertiesString = properties.entries.stream()
            .map { e: Map.Entry<String?, Any?> -> e.key.toString() + "=" + e.value }
            .collect(Collectors.joining(";"))

        // kinda hacky. this will call #setProperties on the datasource, which will append these options
        // onto the connections.
        config.addDataSourceProperty("properties", propertiesString)
    }

    // use backticks for quotes
    override val statementProcessor: Function<String, String>
        get() = Function<String, String> { s -> s.replace('\'', '`') } // use backticks for quotes
}