package com.deckerpw.prism

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.*
import javax.swing.border.EmptyBorder

class MainFrame : JFrame("Prism Worlds") {

    init {
        size = Dimension(500, 700)
        setLocationRelativeTo(null) // Zentriert das Fenster

        val library = Library("C:/Users/decke/AppData/Roaming/PrismLauncher/instances")
        val jlist = JList(library.worldList.toTypedArray())

        jlist.setCellRenderer { _, value, index, isSelected, _ ->
            JPanel(BorderLayout(5, 5)).apply {
                border = EmptyBorder(0, 2, 2, 0)
                val world = value as World;
                background = if (isSelected) jlist.selectionBackground else jlist.background
                isOpaque = true
                add(WorldImage(world), BorderLayout.WEST)
                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    background = if (isSelected) jlist.selectionBackground else jlist.background
                    isOpaque = true
                    add(JLabel(world.name).apply {
                        font = font.deriveFont(java.awt.Font.BOLD, 18f)
                    })
                    add(JLabel(world.formattedDate))
                    add(JLabel("${world.gameMode}, ${if (world.cheatsEnabled) "Commands, " else ""}${world.instance.name}"))
                })
            }
        }
        jlist.fixedCellHeight = 102
        jlist.selectionMode = ListSelectionModel.SINGLE_SELECTION

        jlist.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 2) {
                    val index = jlist.locationToIndex(e.point)
                    val world = jlist.model.getElementAt(index) as? World
                    world?.let {
                        it.run()
                        JOptionPane.showMessageDialog(this@MainFrame, "Welt wird gestartet: ${it.name}")
                        this@MainFrame.dispose()
                    }
                }
            }
        })


        add(JScrollPane(jlist))

        isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE
    }

}

class WorldImage(val world: World) : JComponent() {

    init {
        preferredSize = Dimension(100, 100)
        minimumSize = preferredSize
    }

    override fun paintComponent(g: Graphics?) {
        world.image.let {
            g?.drawImage(it, 0, 0, 100, 100, null)
        }
    }

}