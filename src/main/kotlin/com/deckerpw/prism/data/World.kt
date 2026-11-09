package com.deckerpw.prism.data

import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.io.path.name

class World(
    var name: String,
    val instance: Instance,
    val gameMode: String,
    val cheatsEnabled: Boolean,
    val lastPlayed: Long,
    val playTime: Long,
    val path: Path,
    val image: BufferedImage? = null
) {

    var valid: Boolean = true
    val formattedDate: String get() = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(lastPlayed))
    val formattedPlayTime: String get() = formatTicks(playTime)

    fun formatTicks(ticks: Long): String {
        return String.format("%.2f Stunden", (ticks / 20f / 3600f))
    }

    fun run() {
        if (!valid) return
        ProcessBuilder(
            "C:/Windows/System32/cmd.exe",
            "/C",
            "C:\\Users\\decke\\AppData\\Local\\Programs\\PrismLauncher\\prismlauncher.exe",
            "--launch",
            "\"${instance.localName}\"",
            "--world",
            "\"${path.name}\""
        ).directory(File("C:/Users/decke/AppData/Local/Programs/PrismLauncher")).start()
    }

    fun runInstance() {
        if (!valid) return
        ProcessBuilder(
            "C:/Windows/System32/cmd.exe",
            "/C",
            "C:\\Users\\decke\\AppData\\Local\\Programs\\PrismLauncher\\prismlauncher.exe",
            "--launch",
            "\"${instance.localName}\""
        ).directory(File("C:/Users/decke/AppData/Local/Programs/PrismLauncher")).start()
    }

    fun delete(): Boolean {
        if (!valid) return false
        val success = path.toFile().deleteRecursively()
        valid = success
        return success
    }

    fun rename(newName: String) {
        if (!valid) return

        val level = Paths.get(path.toString(), "level.dat")
        val namedTag = NBTUtil.read(level.toString())


        val compound = namedTag.tag as CompoundTag
        val dataTag = compound.getCompoundTag("Data")
        dataTag.putString("LevelName", newName)

        NBTUtil.write(namedTag, level.toString())

        name = newName
    }
}