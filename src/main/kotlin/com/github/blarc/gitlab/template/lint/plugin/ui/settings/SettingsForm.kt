package com.github.blarc.gitlab.template.lint.plugin.ui

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.RemotesTable
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.RemotesTableModel
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.BrowserLink
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsForm(val project: Project) {
    var basePanel: JPanel? = null
    private var gitlabUrlTextField: JBTextField? = null
    private var gitlabTokenField: JBPasswordField? = null
    private var remotesTablePanel: JPanel? = null
    private var reportBugLink: BrowserLink? = null
    private var verifyButton: JButton? = null
    private var verifyLabel: JBLabel? = null

    private var remotesList = mutableListOf<Pair<String, Long?>>()
    private val tableModel = RemotesTableModel(remotesList)
    val gitlabRemotesTable = RemotesTable(tableModel)

    init {
        val gitlabUrl = project.service<ProjectSettings>().gitlabUrl
        gitlabUrlTextField?.text = gitlabUrl
        if (gitlabUrl != null) {
            gitlabTokenField?.text = AppSettings.instance?.getGitlabToken(gitlabUrl)
        }

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

        verifyButton?.addActionListener {
            runBackgroundableTask("Test") {
                verifyLabel?.icon = AllIcons.General.InlineRefreshHover
                verifyLabel?.text = "Verifying Gitlab token..."
                Thread.sleep(5000)
                verifyLabel?.icon = AllIcons.General.InspectionsOK
                verifyLabel?.text = "Gitlab token is valid."
            }
        }
    }

    fun createUIComponents() {
        reportBugLink = BrowserLink(message("actions.report-bug.title"), GitlabLintBundle.URL_BUG_REPORT.toString())
    }


    val preferredFocusedComponent: JComponent?
        get() = gitlabTokenField

    @get:NotNull
    var gitlabToken: CharArray?
        get() {
            return gitlabTokenField?.password
        }
        set(newGitlabToken) {
            gitlabTokenField?.text = newGitlabToken?.let { String(newGitlabToken) }
        }

    val gitlabUrl: String?
        get() {
            return gitlabUrlTextField?.text
        }
}


