package com.deckerpw.prism.ui

import javax.swing.JLabel

object StatusManager {

    var statusLabel: JLabel? = null

    fun post(text: String){
        statusLabel?.text = text
        println("[STATUS] $text")
    }

}