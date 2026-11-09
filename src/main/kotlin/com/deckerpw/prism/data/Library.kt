package com.deckerpw.prism.data

import com.deckerpw.prism.ui.StatusManager
import com.deckerpw.prism.sorting
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.name

class Library(val folder: String) {

    val instances: MutableList<Instance>
    val worldList = mutableListOf<World>()

    init {
        instances = findInstances().toMutableList()
        println(instances)
        for (instance in instances) {
            worldList.addAll(instance.findWorlds())
        }
        when (sorting)
        {
            0 -> worldList.sortBy { it.name.lowercase() }
            1 -> worldList.sortByDescending { it.lastPlayed }
            2 -> worldList.sortByDescending { it.playTime }
        }
        println(worldList)
    }

    fun findInstances(): List<Instance> {
        StatusManager.post("Finding Instances: 0")
        val dir = File(folder)
        val list = mutableListOf<Instance>()
        dir.listFiles()?.forEach { file ->
            val configFile = Paths.get(file.path, "instance.cfg").toFile()
            var name: String? = null;
            var icon: String? = null;
            if (configFile.exists()) {
                val content = configFile.readText()
                content.lines().forEach { line ->
                    if (line.startsWith("name=")) {
                        name = line.substringAfter("name=")
                    }
                    if (line.startsWith("iconKey=")) {
                        icon = line.substringAfter("iconKey=")
                    }
                }
            }
            if (name != null) {
                list.add(Instance(name, file.path, file.name, icon))
                StatusManager.post("Finding Instances: ${list.size}")
            }
        }
        return list.toList()
    }

}