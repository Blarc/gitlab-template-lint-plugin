package com.github.blarc.gitlab.template.lint.plugin.ui

import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBPasswordField
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsForm {
    var basePanel: JPanel? = null
    private var gitlabTokenField: JBPasswordField? = null
    private var remotesTablePanel: JPanel? = null

    var remotesList = mutableListOf<Pair<String, Long>>()
    private val tableModel = RemotesTableModel(remotesList)
    val gitlabRemotesTable = RemotesTable(tableModel)

    init {
        remotesTablePanel!!.add(
            ToolbarDecorator.createDecorator(gitlabRemotesTable)
                .setAddAction { gitlabRemotesTable.addRemote() }
                .setRemoveAction { gitlabRemotesTable.removeSelectedRemotes() }
                .setEditAction { gitlabRemotesTable.editRemote() }
                .createPanel(),
            BorderLayout.CENTER
        )

        object : DoubleClickListener() {
            override fun onDoubleClick(e: MouseEvent): Boolean {
                return gitlabRemotesTable.editRemote()
            }
        }.installOn(gitlabRemotesTable)
    }


    val preferredFocusedComponent: JComponent?
        get() = gitlabTokenField

    @get:NotNull
    var gitlabToken: CharArray?
        get() = gitlabTokenField?.password
        set(newText) {
            gitlabTokenField?.text = newText.toString()
        }
}


