package com.deckerpw.prism


import com.deckerpw.poolbox.PoolApp
import com.deckerpw.poolbox.config.intConfig
import com.deckerpw.poolbox.config.stringConfig
import com.deckerpw.prism.data.Instance
import com.deckerpw.prism.data.Server
import org.json.JSONObject

val VERSION = (System.getProperty("VERSION") ?: "0").apply {
    println("Version: $this")
}
const val IDENTIFIER = "com.deckerpw.prism"
val poolApp = PoolApp(VERSION, IDENTIFIER)
private val config = poolApp.config

var uuid by config.stringConfig("uuid", "")
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