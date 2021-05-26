package me.tropicalshadow.aurorarpg.database.connection.utils

import java.util.*


data class StorageCredentials(
    private val address: String,
    private val database: String,
    private val username: String,
    private val password: String,
    val maxPoolSize: Int,
    val minIdleConnections: Int,
    val maxLifetime: Int,
    val keepAliveTime: Int,
    val connectionTimeout: Int,
    val properties: Map<String, String>
) {

    fun getAddress(): String {
        return Objects.requireNonNull(address, "address")
    }

    fun getDatabase(): String {
        return Objects.requireNonNull(database, "database")
    }

    fun getUsername(): String {
        return Objects.requireNonNull(username, "username")
    }

    fun getPassword(): String {
        return Objects.requireNonNull(password, "password")
    }

}