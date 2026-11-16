package com.deckerpw.prism

import com.deckerpw.prism.ui.frame.MainFrame
import com.deckerpw.prism.ui.frame.initialSetup
import com.deckerpw.prism.ui.frame.validateSettings
import com.formdev.flatlaf.FlatDarkLaf
import javax.swing.UIManager

fun main() {
    FlatDarkLaf.setup();
    UIManager.put( "TitlePane.unifiedBackground", false);

    start()
}

fun start(){
    if (!validateSettings())
        initialSetup(null){ restart ->
            if (restart)
                return@initialSetup
            start()
        }
    else
        MainFrame()
}