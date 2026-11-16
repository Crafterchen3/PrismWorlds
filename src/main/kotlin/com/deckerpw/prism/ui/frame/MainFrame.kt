package com.deckerpw.prism.ui.frame

import com.deckerpw.poolbox.about.AboutDialog
import com.deckerpw.prism.data.Instance
import com.deckerpw.prism.data.Library
import com.deckerpw.prism.data.Server
import com.deckerpw.prism.data.World
import com.deckerpw.prism.instancesDir
import com.deckerpw.prism.poolApp
import com.deckerpw.prism.saveServers
import com.deckerpw.prism.servers
import com.deckerpw.prism.sorting
import com.deckerpw.prism.start
import com.deckerpw.prism.ui.component.WorldImage
import com.deckerpw.prism.ui.component.ServerImage
import com.formdev.flatlaf.FlatClientProperties
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JCheckBoxMenuItem
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTextField
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder

class MainFrame : JFrame("Prism Worlds") {

    init {
        size = Dimension(500, 700)
        setLocationRelativeTo(null) // Zentriert das Fenster
        setIcon()

        val loadingFrame = LoadingFrame()
        Thread.sleep(10)
        val library = Library(instancesDir)
        val worldListModel = DefaultListModel<World>().apply { library.worldList.forEach { addElement(it) } }
        val serverListModel = DefaultListModel<Server>().apply { servers.forEach { addElement(it) } }


        val menuBar = JMenuBar()

        //Menu Bar
        val fileMenu = JMenu("File")
        fileMenu.add(JMenuItem("Settings").apply {
            addActionListener {
                settings(this@MainFrame){ restart ->
                    if (restart)
                        this@MainFrame.dispose()
                    else{
                        this@MainFrame.dispose()
                        SwingUtilities.invokeLater {
                            start()
                        }
                    }
                }
            }
        })
        fileMenu.addSeparator()
        fileMenu.add(JMenuItem("Add Server").apply {
            addActionListener {
                showAddServerDialog(serverListModel, library)
            }
        })
        fileMenu.add(JMenuItem("Exit").apply {
            addActionListener { this@MainFrame.dispose() }
        })
        menuBar.add(fileMenu)
        val sortMenu = JMenu("Sorting")
        sortMenu.add(JCheckBoxMenuItem("By Name", sorting == 0).apply {
            addActionListener {
                val worlds = mutableListOf<World>()
                for (i in 0 until worldListModel.size()) {
                    worlds.add(worldListModel.getElementAt(i))
                }
                worlds.sortBy { it.name.lowercase() }
                worldListModel.clear()
                worlds.forEach { worldListModel.addElement(it) }

                sorting = 0
                isSelected = true
                // unselect other menu items
                for (i in 0 until sortMenu.itemCount) {
                    val item = sortMenu.getItem(i)
                    if (item != this) {
                        item.isSelected = false
                    }
                }
            }
        })
        sortMenu.add(JCheckBoxMenuItem("By Last Played", sorting == 1).apply {
            addActionListener {
                val worlds = mutableListOf<World>()
                for (i in 0 until worldListModel.size()) {
                    worlds.add(worldListModel.getElementAt(i))
                }
                worlds.sortByDescending { it.lastPlayed }
                worldListModel.clear()
                worlds.forEach { worldListModel.addElement(it) }

                sorting = 1
                isSelected = true
                // unselect other menu items
                for (i in 0 until sortMenu.itemCount) {
                    val item = sortMenu.getItem(i)
                    if (item != this) {
                        item.isSelected = false
                    }
                }
            }
        })
        sortMenu.add(JCheckBoxMenuItem("By Playtime", sorting == 2).apply {
            addActionListener {
                val worlds = mutableListOf<World>()
                for (i in 0 until worldListModel.size()) {
                    worlds.add(worldListModel.getElementAt(i))
                }
                worlds.sortByDescending { it.playTime }
                worldListModel.clear()
                worlds.forEach { worldListModel.addElement(it) }

                sorting = 2
                isSelected = true
                // unselect other menu items
                for (i in 0 until sortMenu.itemCount) {
                    val item = sortMenu.getItem(i)
                    if (item != this) {
                        item.isSelected = false
                    }
                }
            }
        })
        menuBar.add(sortMenu)
        val helpMenu = JMenu("Help")
        helpMenu.add(JMenuItem("Look for Updates...").apply {
            addActionListener {
                poolApp.updater.checkForUpdates(this@MainFrame)
            }
        })
        helpMenu.addSeparator()
        helpMenu.add(JMenuItem("About").apply {
            addActionListener {
                AboutDialog(this@MainFrame, poolApp)
            }
        })
        menuBar.add(helpMenu)
        jMenuBar = menuBar

        val jTabbedPane = JTabbedPane()
        jTabbedPane.putClientProperty("JTabbedPane.tabAreaAlignment", FlatClientProperties.TABBED_PANE_ALIGN_FILL)

        val worldjlist = JList(worldListModel)

        worldjlist.setCellRenderer { _, value, index, isSelected, _ ->
            JPanel(BorderLayout(5, 5)).apply {
                border = EmptyBorder(0, 2, 2, 0)
                val world = value as World;
                background = if (isSelected) worldjlist.selectionBackground else worldjlist.background
                isOpaque = true
                add(WorldImage(world), BorderLayout.WEST)
                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    background = if (isSelected) worldjlist.selectionBackground else worldjlist.background
                    isOpaque = true
                    add(JLabel(world.name).apply {
                        font = font.deriveFont(Font.BOLD, 18f)
                    })
                    add(JLabel("${world.formattedDate}, Played: ${world.formattedPlayTime}"))
                    add(JLabel("${world.gameMode}, ${if (world.cheatsEnabled) "Commands, " else ""}${world.instance.name}"))
                })
            }
        }
        worldjlist.selectionMode = ListSelectionModel.SINGLE_SELECTION

        worldjlist.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && e.button == 1) {
                    val index = worldjlist.locationToIndex(e.point)
                    val world = worldjlist.model.getElementAt(index) as? World
                    world?.let {
                        it.run()
                        JOptionPane.showMessageDialog(this@MainFrame, "Starting World: ${it.name}")
                        this@MainFrame.dispose()
                    }
                }else if (e.button == 3){
                    val index = worldjlist.locationToIndex(e.point)
                    val world = worldjlist.model.getElementAt(index) as? World
                    val contextMenu = JPopupMenu().apply {
                        add(JMenuItem("Run World").apply { addActionListener { world?.run(); this@MainFrame.dispose() } })
                        add(JMenuItem("Run Instance").apply { addActionListener { world?.runInstance(); this@MainFrame.dispose() } })
                        addSeparator()
                        add(JMenuItem("Open World Folder").apply { addActionListener { Desktop.getDesktop().open(world?.path?.toFile()) } })
                        add(JMenuItem("Open Instance Folder").apply { addActionListener { Desktop.getDesktop().open(
                            File(
                                world?.instance?.path!!
                            )
                        ) } })
                        addSeparator()
                        add(JMenuItem("Rename").apply { addActionListener {
                            val neuerName = JOptionPane.showInputDialog(
                                this@MainFrame,
                                "Enter new name:",
                                world?.name ?: ""
                            )
                            if (!neuerName.isNullOrBlank()) {
                                world?.rename(neuerName)
                                worldjlist.repaint()
                            }
                        } })
                        add(JMenuItem("Delete").apply { addActionListener {
                            val confirm = JOptionPane.showConfirmDialog(
                                this@MainFrame,
                                "Do you really want to delete this World?",
                                "Confirm deletion",
                                JOptionPane.YES_NO_OPTION
                            )
                            if (confirm == JOptionPane.YES_OPTION) {
                                if(world?.delete() ?: false) {
                                    worldListModel.remove(index)
                                }
                            }
                        } })
                    }
                    worldjlist.selectedIndex = index
                    contextMenu.show(worldjlist, e.x, e.y)
                }
            }
        })

        val serverjlist = JList(serverListModel)

        serverjlist.setCellRenderer { _, value, index, isSelected, _ ->
            JPanel(BorderLayout(5, 5)).apply {
                border = EmptyBorder(0, 2, 2, 0)
                val server = value as Server;
                background = if (isSelected) serverjlist.selectionBackground else serverjlist.background
                isOpaque = true
                add(ServerImage(), BorderLayout.WEST)
                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    background = if (isSelected) serverjlist.selectionBackground else serverjlist.background
                    isOpaque = true
                    add(JLabel(server.name).apply {
                        font = font.deriveFont(Font.BOLD, 18f)
                    })
                    add(JLabel(server.ip))
                    add(JLabel(server.preferredInstance.name))
                })
            }
        }
        serverjlist.selectionMode = ListSelectionModel.SINGLE_SELECTION

        serverjlist.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && e.button == 1) {
                    val index = serverjlist.locationToIndex(e.point)
                    val server = serverjlist.model.getElementAt(index) as? Server
                    server?.let {
                        it.run()
                        JOptionPane.showMessageDialog(this@MainFrame, "Starting server: ${it.name}")
                        this@MainFrame.dispose()
                    }
                }else if (e.button == 3){
                    val index = serverjlist.locationToIndex(e.point)
                    val server = serverjlist.model.getElementAt(index) as? Server
                    val contextMenu = JPopupMenu().apply {
                        add(JMenuItem("Run Server").apply { addActionListener { server?.run(); this@MainFrame.dispose() } })
                        add(JMenuItem("Run Instance").apply { addActionListener { server?.runInstance(); this@MainFrame.dispose() } })
                        addSeparator()
                        add(JMenuItem("Open Instance Folder").apply { addActionListener { Desktop.getDesktop().open(File(server?.preferredInstance?.path!!)) } })
                        addSeparator()
                        add(JMenuItem("Rename").apply { addActionListener {
                            val neuerName = JOptionPane.showInputDialog(
                                this@MainFrame,
                                "Enter new name:",
                                server?.name ?: ""
                            )
                            if (!neuerName.isNullOrBlank()) {
                                server?.name = neuerName
                                servers[index] = server!!
                                saveServers()
                                serverjlist.repaint()
                            }
                        } })
                        add(JMenuItem("Remove").apply { addActionListener {
                            val confirm = JOptionPane.showConfirmDialog(
                                this@MainFrame,
                                "Do you want to remove this server?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION
                            )
                            if (confirm == JOptionPane.YES_OPTION) {
                                servers.removeAt(index)
                                saveServers()
                                serverListModel.remove(index)
                            }
                        } })
                    }
                    serverjlist.selectedIndex = index
                    contextMenu.show(serverjlist, e.x, e.y)
                }
            }
        })


        jTabbedPane.addTab("Worlds", JScrollPane(worldjlist).apply { border = null })
        jTabbedPane.addTab("Servers", JScrollPane(serverjlist).apply { border = null })
        add(jTabbedPane)
        loadingFrame.dispose()
        isVisible = true
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    private fun showAddServerDialog(serverListModel: DefaultListModel<Server>, library: Library) {
        val dialog = JDialog(this, "Add Server", true)
        dialog.layout = BorderLayout(10, 10)

        val instanceComboBox =
            JComboBox(library.instances.toTypedArray())
        val ipField = JTextField()
        val nameField = JTextField()
        val formPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(10, 10, 10, 10)

            add(JLabel("Server Name:").apply {
                horizontalAlignment = SwingConstants.LEFT
            })
            add(nameField)

            add(Box.createVerticalStrut(10))

            add(JLabel("Server IP:").apply {
                horizontalAlignment = SwingConstants.LEFT
            })
            add(ipField)

            add(Box.createVerticalStrut(10))

            add(JLabel("Preferred Instance:").apply {
                horizontalAlignment = SwingConstants.LEFT
            })
            add(instanceComboBox)
        }

        dialog.add(formPanel, BorderLayout.CENTER)

        val buttonPanel = JPanel().apply {
            add(JButton("Add").apply {
                addActionListener {
                    val name = nameField.text
                    val ip = ipField.text
                    val instance = instanceComboBox.selectedItem as Instance

                    if (name.isNotBlank() && ip.isNotBlank()) {
                        val newServer = Server(name, ip, instance)
                        servers.add(newServer)
                        serverListModel.addElement(newServer)
                        saveServers()
                        dialog.dispose()
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Please fill out the whole form!")
                    }
                }
            })
            add(JButton("Cancel").apply {
                addActionListener { dialog.dispose() }
            })
        }

        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.pack()
        dialog.setLocationRelativeTo(this)
        dialog.isVisible = true
    }

    fun setIcon() {
        try {
            val image = ImageIO.read(MainFrame::class.java.getResource("/PrismWorlds.png"))

            this.iconImage = image
        }catch (e: Exception){

        }
    }

}