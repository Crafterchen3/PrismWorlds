package com.deckerpw.prism.ui.component

import com.deckerpw.prism.data.World
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JComponent

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