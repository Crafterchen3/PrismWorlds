package com.deckerpw.prism


import com.deckerpw.poolbox.PoolApp
import com.deckerpw.poolbox.config.intConfig
import com.deckerpw.poolbox.config.stringConfig
import com.deckerpw.prism.data.Instance
import com.deckerpw.prism.data.Server
import com.deckerpw.prism.ui.frame.MainFrame
import org.json.JSONObject
import javax.swing.ImageIcon

val VERSION = (System.getProperty("VERSION") ?: "0").apply {
    println("Version: $this")
}
const val IDENTIFIER = "com.deckerpw.prism"
val poolApp = PoolApp(VERSION, IDENTIFIER, info = PoolApp.AppInfo(
    name = "PrismWorlds",
    description = "A simple App to easily start any Minecraft world or Server of any instance of the Prism Launcher.",
    copyright = "© 2026 Paul Decker. All Rights Reserved.",
    icon = MainFrame::class.java.getResource("/PrismWorlds.png")?.let { ImageIcon(it) }
))
private val config = poolApp.config

val prismLauncherFileDelegate = config.stringConfig("prism_launcher_file","")
val instancesDirDelegate = config.stringConfig("instances_dir", "")
val uuidDelegate = config.stringConfig("uuid", "")

val prismLauncherFile by prismLauncherFileDelegate
val instancesDir by instancesDirDelegate
val uuid by uuidDelegate

var sorting by config.intConfig("sorting", 0)
var servers = mutableListOf<Server>().apply {
    if (config.hasKey("servers")) {
        config.getArray<JSONObject>("servers").forEach {
            add(
                Server(
                    it.getString("name"),
                    it.getString("ip"),
                    Instance(
                        it.getJSONObject("preferredInstance").getString("name"),
                        it.getJSONObject("preferredInstance").getString("path"),
                        it.getJSONObject("preferredInstance").getString("localName")
                    )
                )
            )
        }
    }
}

fun saveServers() {
    config.putArray("servers", servers)
}