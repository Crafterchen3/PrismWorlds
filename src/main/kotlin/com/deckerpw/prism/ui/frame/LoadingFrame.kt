package com.deckerpw.prism.ui.frame

import com.deckerpw.prism.ui.StatusManager
import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder

class LoadingFrame : JDialog() {

    val jLabel = JLabel("").apply {
        horizontalAlignment = SwingConstants.CENTER
    }

    init {
        setSize(300,130)
        setLocationRelativeTo(null)
        title = "PrismWorlds"

        layout = BorderLayout()
        add(JPanel(BorderLayout()).apply {
            add(JProgressBar().apply {
                isIndeterminate = true
            }, BorderLayout.SOUTH)
            add(jLabel, BorderLayout.CENTER)
            border = EmptyBorder(5, 20, 20, 20)
        }, BorderLayout.CENTER)

        StatusManager.statusLabel = jLabel
        isVisible = true
    }



}