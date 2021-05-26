package me.tropicalshadow.aurorarpg.utils

import me.tropicalshadow.aurorarpg.AuroraRPG
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class FileHandler(val fileName: String) {

    var config:YamlConfiguration
    var file: File

    init {
        val plugin = AuroraRPG.plugin
        val dataFolder = plugin.dataFolder
        if(!dataFolder.exists())dataFolder.mkdir()

        file = File(dataFolder,fileName)
        if(!file.exists())plugin.saveResource(fileName,false)
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save(){
        if(!file.exists())
            AuroraRPG.plugin.saveResource(fileName,false)
        config.save(file)
    }
    fun reload(){
        config.load(file)
    }

}