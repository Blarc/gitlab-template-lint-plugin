package com.github.blarc.gitlab.template.lint.plugin.ui

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabDetector
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
    private var resolveButton: JButton? = null
    private var verifyLabel: JBLabel? = null

    private var remotesList = mutableListOf<Pair<String, Long?>>()
    private val tableModel = RemotesTableModel(remotesList)
    val gitlabRemotesTable = RemotesTable(tableModel)

    init {

        initGitlabUrlField()
        initResolveButton()
        initVerifyButton()
        initRemotesTable()

    }

    private fun initGitlabUrlField() {
        val gitlabUrl = project.service<ProjectSettings>().gitlabUrl
        gitlabUrlTextField?.text = gitlabUrl
        if (gitlabUrl != null) {
            gitlabTokenField?.text = AppSettings.instance?.getGitlabToken(gitlabUrl)
        }
    }

    private fun initResolveButton() {
        resolveButton?.addActionListener {
            runBackgroundableTask(message("settings.resolve.running")) {
                verifyLabel?.icon = AllIcons.General.InlineRefreshHover
                verifyLabel?.text = message("settings.resolve.running")
                val gitlabUrl = project.service<GitlabDetector>().detect()
                if (gitlabUrl == null) {
                    verifyLabel?.icon = AllIcons.General.InspectionsError
                    verifyLabel?.text = message("settings.resolve.invalid")
                }
                else {
                    gitlabUrlTF = gitlabUrl.toString()
                    gitlabTokenTF = AppSettings.instance?.getGitlabToken(gitlabUrl.toString())
                    verifyLabel?.icon = AllIcons.General.InspectionsOK
                    verifyLabel?.text = message("settings.resolve.valid")
                }
            }
        }
    }

    private fun initVerifyButton() {
        verifyButton?.addActionListener {
            runBackgroundableTask(message("settings.verify.running")) {
                if (gitlabUrlTF.isNullOrEmpty()) {
                    verifyLabel?.icon = AllIcons.General.InspectionsError
                    verifyLabel?.text = message("settings.verify.gitlab-url-not-set")
                }
                else {
                    verifyLabel?.icon = AllIcons.General.InlineRefreshHover
                    verifyLabel?.text = message("settings.verify.running")
                    try {
                        project.service<Gitlab>().getVersion(gitlabTokenTF!!).get()
                        verifyLabel?.icon = AllIcons.General.InspectionsOK
                        verifyLabel?.text = message("settings.verify.valid")
                    }
                    catch (e: Exception) {
                        verifyLabel?.icon = AllIcons.General.InspectionsError
                        verifyLabel?.text = message("settings.verify.invalid")
                    }
                }
            }
        }
    }

    private fun initRemotesTable() {
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

    fun createUIComponents() {
        reportBugLink = BrowserLink(message("actions.report-bug.title"), GitlabLintBundle.URL_BUG_REPORT.toString())
    }


    val preferredFocusedComponent: JComponent?
        get() = gitlabTokenField

    @get:NotNull
    var gitlabTokenTF: String?
        get() {
            return gitlabTokenField?.password?.let { String(it) }
        }
        set(newGitlabToken) {
            gitlabTokenField?.text = newGitlabToken
        }

    var gitlabUrlTF: String?
        get() {
            return gitlabUrlTextField?.text
        }
        set(newGitlabUrl) {
            gitlabUrlTextField?.text = newGitlabUrl
        }
}


