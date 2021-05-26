package me.tropicalshadow.aurorarpg.utils

import com.google.gson.Gson
import me.tropicalshadow.aurorarpg.AuroraRPG
import me.tropicalshadow.aurorarpg.database.connection.ConnectionFactory
import me.tropicalshadow.aurorarpg.dataclasses.AttributeData
import me.tropicalshadow.aurorarpg.dataclasses.PlayerData
import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.SQLException

import java.util.UUID
import kotlin.collections.LinkedHashMap

class PlayerDataService(val database: ConnectionFactory) : Service<PlayerData> {

    private val PLAYER_DELETE = "DELETE FROM `players` WHERE uuid=?"
    private val PLAYER_INSERT = "INSERT INTO `players` (uuid, username, jsonData) VALUES(?, ?, ?)"
    private val PLAYER_UPDATE_USERNAME_FOR_UUID = "UPDATE `players` SET username=?, jsonData=? WHERE uuid=?"
    private val PLAYER_SELECT_BY_UUID = "SELECT username,jsonData  FROM `players` WHERE uuid=?"

    companion object {
        @JvmStatic val CACHE = LinkedHashMap<UUID,PlayerData>()
    }

    override fun save(t: PlayerData) {
        saveUser(t)
    }
    fun JsonIfy(data:AttributeData?) : String{
        val gson = Gson()
        if(data == null)
            return gson.toJson(AttributeData())
        return gson.toJson(data)
    }
    fun unJsonIfy(data: String) : AttributeData{
        val gson = Gson()
        AuroraRPG.plugin.logger.info("data | "+data)
        return gson.fromJson(data,AttributeData::class.java)
    }


    @Throws(SQLException::class)
    fun loadUser(uniqueId: UUID): PlayerData {
        val username = Bukkit.getOfflinePlayer(uniqueId).name
        val data = PlayerData(username!!,uniqueId, AttributeData())
        var playerDataSql: SqlPlayerData?
        database.connection.use { c ->
            playerDataSql = c?.let { selectPlayerData(it, data.uniqueId) }
        }
        if (playerDataSql == null) {
            saveUser(data)
        }else{
            data.username = playerDataSql!!.username
            data.uniqueId = UUID.fromString(playerDataSql!!.uniqueId)
            data.jsonData = unJsonIfy(playerDataSql!!.jsonData)
        }
        return data
    }

    @Throws(SQLException::class)
    fun saveUser(data: PlayerData) {
        var playerData: SqlPlayerData? = null
        database.connection?.let { playerData = selectPlayerData(it, data.uniqueId) }
        if(playerData == null){

            database.connection.use { c ->
                if (c != null) {
                    insertPlayerData(c, data.uniqueId, SqlPlayerData(username = data.username, uniqueId = data.uniqueId.toString(), jsonData = JsonIfy(AttributeData()))
                    )
                }
            }
        }else{
            updatePlayerData(data.uniqueId)
        }
    }

    override fun delete(uuid: UUID) {
        deletePlayerData(uuid)
        CACHE.remove(uuid)
    }

    override fun update(uuid: UUID) {
        var user: PlayerData? = CACHE.getOrDefault(uuid,null)
        if(user == null){
            user =  loadUser(uuid)
            CACHE[uuid] = user
        }else {
            updatePlayerData(uuid)
        }
    }

    fun updatePlayerData(uuid: UUID?){
        database.connection.use { c ->
            c!!.prepareStatement(PLAYER_UPDATE_USERNAME_FOR_UUID).use { ps ->
                ps.setString(1,CACHE[uuid]!!.username)
                ps.setString(2, JsonIfy(CACHE[uuid]!!.jsonData))
                ps.setString(3, uuid.toString())
                ps.execute()
            }
        }
    }


    override fun find(uuid: UUID): PlayerData {
        var user: PlayerData? = CACHE.getOrDefault(uuid,null)
        if(user == null){
            user =  loadUser(uuid)
            CACHE[uuid] = user
        }
        return user
    }

    override fun removeFromCache(uuid: UUID) {
        CACHE.remove(uuid)
    }
    @Throws(SQLException::class)
    private fun selectPlayerData(c: Connection, user: UUID): SqlPlayerData? {
        c.prepareStatement(PLAYER_SELECT_BY_UUID).use { ps ->
            ps.setString(1, user.toString())
            ps.executeQuery().use { rs ->
                return if (rs.next()) {
                    SqlPlayerData(user.toString(),rs.getString("username"),rs.getString("jsonData"))
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun deletePlayerData(uniqueId: UUID) {
        database.connection.use { c ->
            c!!.prepareStatement(PLAYER_DELETE).use { ps ->
                ps.setString(1, uniqueId.toString())
                ps.execute()
            }
        }
    }

    @Throws(SQLException::class)
    private fun insertPlayerData(c: Connection, user: UUID, data: SqlPlayerData) {
        // insert
        c.prepareStatement(PLAYER_INSERT).use { ps ->
            ps.setString(1, user.toString())
            ps.setString(2, data.username)
            ps.setString(3, data.jsonData)
            ps.execute()
        }

    }
    @Throws(SQLException::class)
    private fun tableExists(connection: Connection, table: String): Boolean {
        connection.metaData.getTables(connection.catalog, null, "%", null).use { rs ->
            while (rs.next()) {
                if (rs.getString(3).equals(table, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
    private class SqlPlayerData internal constructor(val uniqueId: String, val username: String, val jsonData: String)


}