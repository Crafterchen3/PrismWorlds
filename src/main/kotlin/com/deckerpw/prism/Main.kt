package com.deckerpw.prism

import com.deckerpw.prism.ui.frame.MainFrame
import com.formdev.flatlaf.FlatDarkLaf
import javax.swing.UIManager

fun main() {
    FlatDarkLaf.setup();
    UIManager.put( "TitlePane.unifiedBackground", false);

    MainFrame()
}