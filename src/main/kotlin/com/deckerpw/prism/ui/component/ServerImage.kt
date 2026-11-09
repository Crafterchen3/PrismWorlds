package com.deckerpw.prism.ui.component

import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JComponent

class ServerImage() : JComponent() {
    companion object{
        val image: BufferedImage? = ImageIO.read(ServerImage::class.java.getResource("/server.png"))
    }

    init {
        preferredSize = Dimension(100, 100)
        minimumSize = preferredSize
    }

    override fun paintComponent(g: Graphics?) {
        g?.drawImage(image, 0, 0, 100, 100, null)
    }

}