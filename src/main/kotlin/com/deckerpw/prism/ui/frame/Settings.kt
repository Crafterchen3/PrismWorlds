package com.deckerpw.prism.ui.frame

import com.deckerpw.poolbox.settings.SectionBuilder
import com.deckerpw.poolbox.settings.SingleSettingsFrame
import com.deckerpw.prism.*
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame

private fun SectionBuilder.general() {
    header("Setup")
    fileTextField(
        label = "Prism Launcher Executable",
        delegate = prismLauncherFileDelegate,
        mode = JFileChooser.FILES_ONLY,
        title = "Choose Prism Launcher Executable"
    )
    description("Needed to Start the instances")
    fileTextField(
        label = "Instances Directory",
        delegate = instancesDirDelegate,
        mode = JFileChooser.DIRECTORIES_ONLY,
        title = "Choose Instances Directory",
    )
    description("Needed to find all Worlds")
    header("Customization")
    textField(
        label = "Player UUID",
        delegate = uuidDelegate
    )
    description("Optional; Needed to provide Playtime")
}

fun validateSettings(): Boolean {
    return instancesDir.isNotEmpty() &&
            File(instancesDir).exists() &&
            prismLauncherFile.isNotEmpty() &&
            File(prismLauncherFile).exists()

}

fun settings(
    owner: JFrame?,
    onFinish: (Boolean) -> Unit
) =
    SingleSettingsFrame(
        owner = owner,
        title = "Settings",
        onFinish = onFinish
    ) {
        general()
    }

fun initialSetup(
    owner: JFrame?,
    onFinish: (Boolean) -> Unit
) =
    SingleSettingsFrame(
        owner = owner,
        title = "Setup",
        mode = SingleSettingsFrame.SingleSettingsMode(cancel = false, apply = false),
        onFinish = onFinish
    ) {
        header("Welcome to PrismWorlds!","h0")
        description("The best way to play launch Minecraft Worlds")
        general()
    }