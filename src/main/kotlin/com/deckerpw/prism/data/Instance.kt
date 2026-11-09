package com.deckerpw.prism.data

import com.deckerpw.prism.ui.StatusManager
import com.deckerpw.prism.uuid
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import org.json.JSONObject
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.exists

data class Instance(val name: String, val path: String, val localName: String, val iconKey: String? = "") {

    var mcDir: File? = null

    fun findWorlds(): List<World> {
        StatusManager.post("Finding Worlds: $name")
        val possibleDirs = listOf(
            Paths.get(path, ".minecraft").toFile(),
            Paths.get(path, "minecraft").toFile()
        )
        val list = mutableListOf<World>()
        for (dir in possibleDirs) {
            if (dir.exists() && dir.isDirectory) {
                mcDir = dir
                Paths.get(dir.path,"saves").toFile().listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        val icon = Paths.get(file.path, "icon.png")
                        var image: BufferedImage? = null
                        if (icon.exists()) {
                            image = ImageIO.read(icon.toFile())
                        }

                        val jsonPath = Paths.get(file.path, "stats", "${uuid}.json")
                        var playTime = 0L
                        try {
                            if (jsonPath.exists()) {
                                val json = JSONObject(jsonPath.toFile().readText())
                                playTime = json.getJSONObject("stats").getJSONObject("minecraft:custom")
                                    .optLong("minecraft:play_time", 0L)
                            }
                        } catch (e: Exception) {
                        }

                        val level = Paths.get(file.path, "level.dat")
                        if (level.exists()) {
                            val namedTag = NBTUtil.read(level.toString())
                            val compound = namedTag.tag as CompoundTag
                            val dataTag = compound.getCompoundTag("Data")
                            val world = World(
                                name = dataTag.getString("LevelName"),
                                gameMode = if (dataTag.getInt("GameType") == 0) "Survival" else "Creative",
                                cheatsEnabled = dataTag.getBoolean("allowCommands"),
                                lastPlayed = dataTag.getLong("LastPlayed"),
                                image = image,
                                path = file.toPath(),
                                playTime = playTime,
                                instance = this
                            )
                            list.add(world)
                        }
                    }
                }
            }
        }
        return list.toList()
    }

}