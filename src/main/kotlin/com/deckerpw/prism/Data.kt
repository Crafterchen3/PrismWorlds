package com.deckerpw.prism

import net.querz.nbt.io.NBTUtil
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.exists


data class World(
    val name: String,
    val instance: Instance,
    val gameMode: String,
    val cheatsEnabled: Boolean,
    val lastPlayed: Long,
    val image: BufferedImage? = null
) {
    val formattedDate: String get() = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(lastPlayed))

    fun run() {
        ProcessBuilder(
            "C:/Users/decke/AppData/Local/Programs/PrismLauncher/prismlauncher.exe",
            "--launch",
            "\"${instance.name}\"",
            "--world",
            "\"$name\""
        ).directory(File("C:/Users/decke/AppData/Local/Programs/PrismLauncher")).start()
    }
}

data class Instance(val name: String, val path: String) {

    fun findWorlds(): List<World> {
        val dir = Paths.get(path, ".minecraft", "saves").toFile()
        val list = mutableListOf<World>()
        if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    val icon = Paths.get(file.path, "icon.png")
                    var image: BufferedImage? = null
                    if (icon.exists()) {
                        image = ImageIO.read(icon.toFile())
                    }
                    val level = Paths.get(file.path, "level.dat")
                    if (level.exists()) {
                        val namedTag = NBTUtil.read(level.toString())
                        val compound = namedTag.tag as net.querz.nbt.tag.CompoundTag
                        val dataTag = compound.getCompoundTag("Data")
                        val world = World(
                            name = dataTag.getString("LevelName"),
                            gameMode = if (dataTag.getInt("GameType") == 0) "Survival" else "Creative",
                            cheatsEnabled = dataTag.getBoolean("allowCommands"),
                            lastPlayed = dataTag.getLong("LastPlayed"),
                            image = image,
                            instance = this
                        )
                        list.add(world)
                    }
                }
            }
        }
        return list.toList()
    }

}

class Library(val folder: String) {

    val worldList = mutableListOf<World>()

    init {
        val instances = findInstances()
        println(instances)
        for (instance in instances) {
            worldList.addAll(instance.findWorlds())
        }
        worldList.sortByDescending { it.lastPlayed }
        println(worldList)
    }

    fun findInstances(): List<Instance> {
        val dir = File(folder)
        val list = mutableListOf<Instance>()
        dir.listFiles()?.forEach { file ->
            val configFile = Paths.get(file.path, "instance.cfg").toFile()
            if (configFile.exists()) {
                val content = configFile.readText()
                content.lines().forEach { line ->
                    if (line.startsWith("name=")) {
                        val name = line.substringAfter("name=")
                        list.add(Instance(name, file.path))
                    }
                }
            }
        }
        return list.toList()
    }

}

fun main() {
    Library("C:/Users/decke/AppData/Roaming/PrismLauncher/instances")
}

// ## SIMPLIFIED VERSION
//
//data class World(
//    val name: String,
//    val instance: Instance,
//    val gameMode: String,
//    val cheatsEnabled: Boolean,
//    val lastPlayed: Long,
//    val image: BufferedImage? = null
//) {
//    val formattedDate: String
//        get() = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(lastPlayed))
//}
//
//data class Instance(val name: String, val path: String) {
//
//    fun findWorlds(): List<World> {
//        val savesDir = Paths.get(path, ".minecraft", "saves").toFile()
//        if (!savesDir.exists() || !savesDir.isDirectory) return emptyList()
//
//        return savesDir.listFiles()?.mapNotNull { file ->
//            if (!file.isDirectory) return@mapNotNull null
//
//            val levelDat = Paths.get(file.path, "level.dat")
//            if (!levelDat.exists()) return@mapNotNull null
//
//            val dataTag = (NBTUtil.read(levelDat.toString()).tag as net.querz.nbt.tag.CompoundTag)
//                .getCompoundTag("Data")
//
//            World(
//                name = dataTag.getString("LevelName"),
//                gameMode = if (dataTag.getInt("GameType") == 0) "Survival" else "Creative",
//                cheatsEnabled = dataTag.getBoolean("allowCommands"),
//                lastPlayed = dataTag.getLong("LastPlayed"),
//                image = Paths.get(file.path, "icon.png").takeIf { it.exists() }?.let { ImageIO.read(it.toFile()) },
//                instance = this
//            )
//        } ?: emptyList()
//    }
//}
//
//class Library(folder: String) {
//
//    val worldList: List<World> = File(folder).listFiles()
//        ?.mapNotNull { file ->
//            val configFile = Paths.get(file.path, "instance.cfg").toFile()
//            if (!configFile.exists()) return@mapNotNull null
//
//            val name = configFile.readLines().find { it.startsWith("name=") }?.substringAfter("name=")
//            name?.let { Instance(it, file.path) }
//        }
//        ?.flatMap { it.findWorlds() }
//        ?.sortedByDescending { it.lastPlayed }
//        ?: emptyList()
//
//    init {
//        println(worldList)
//    }
//}
//
