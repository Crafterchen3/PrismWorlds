package com.deckerpw.prism.data

import java.io.File

class Server(
    var name: String,
    val ip: String,
    val preferredInstance: Instance
) {

    fun run() {
        ProcessBuilder(
            "C:/Users/decke/AppData/Local/Programs/PrismLauncher/prismlauncher.exe",
            "--launch",
            "\"${preferredInstance.localName}\"",
            "--server",
            "\"${ip}\""
        ).directory(File("C:/Users/decke/AppData/Local/Programs/PrismLauncher")).start()
    }

    fun runInstance() {
        ProcessBuilder(
            "C:/Users/decke/AppData/Local/Programs/PrismLauncher/prismlauncher.exe",
            "--launch",
            "\"${preferredInstance.localName}\""
        ).directory(File("C:/Users/decke/AppData/Local/Programs/PrismLauncher")).start()
    }
}