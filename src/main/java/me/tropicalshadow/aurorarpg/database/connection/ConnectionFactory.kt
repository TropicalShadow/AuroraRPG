package me.tropicalshadow.aurorarpg.database.connection

import me.tropicalshadow.aurorarpg.AuroraRPG

import java.lang.Exception
import java.sql.SQLException
import java.sql.Connection;
import java.util.function.Function;


interface ConnectionFactory {
    val implementationName: String?

    fun init(plugin: AuroraRPG)

    @Throws(Exception::class)
    fun shutdown()
    val meta: Map<Any, Any> get() = emptyMap()
    val statementProcessor: Function<String, String>

    @get:Throws(SQLException::class)
    val connection: Connection?
}