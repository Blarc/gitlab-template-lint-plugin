package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
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
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsForm(val project: Project) {
    var basePanel: JPanel? = null
    private var gitlabUrlComboBox: JComboBox<String>? = null
    private var gitlabRemoteTextField: JBTextField? = null
    private var gitlabTokenField: JBPasswordField? = null
    private var remotesTablePanel: JPanel? = null
    private var reportBugLink: BrowserLink? = null
    private var verifyButton: JButton? = null
    private var verifyLabel: JBLabel? = null
    private var forceHttpsCheckBox: JCheckBox? = null

    private var remotesList = mutableListOf<Pair<String, Long?>>()
    private val tableModel = RemotesTableModel(remotesList)
    val gitlabRemotesTable = RemotesTable(tableModel)

    init {

        initGitlabUrlAndTokenFields()
        initVerifyButton()
        initRemoteField()
        initRemotesTable()
        initForceHttpsCheckBox()

    }

    private fun initGitlabUrlAndTokenFields() {
        val projectSettings = project.service<ProjectSettings>()
        val gitlabUrl = projectSettings.gitlabUrl

        gitlabUrlComboBox?.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                val selectedGitlabUrl = it.item as String?
                if (selectedGitlabUrl != null) {
                    gitlabTokenField?.text = AppSettings.instance?.getGitlabToken(selectedGitlabUrl)
                }
            }
        }

        gitlabUrlComboBox?.model = DefaultComboBoxModel(projectSettings.gitlabUrls.toTypedArray())
        gitlabUrlCB = gitlabUrl
    }

    private fun initRemoteField() {
        val gitlabRemote = project.service<ProjectSettings>().remote
        gitlabRemoteTextField?.text = gitlabRemote
    }

    private fun initVerifyButton() {
        verifyButton?.addActionListener {
            runBackgroundableTask(message("settings.verify.running")) {
                if (gitlabUrlCB.isNullOrEmpty()) {
                    verifyLabel?.icon = AllIcons.General.InspectionsError
                    verifyLabel?.text = message("settings.verify.gitlab-url-not-set")
                }
                else {
                    verifyLabel?.icon = AllIcons.General.InlineRefreshHover
                    verifyLabel?.text = message("settings.verify.running")
                    try {
                        project.service<Gitlab>().getVersion(gitlabUrlCB!!, gitlabTokenTF!!).get()
                        verifyLabel?.icon = AllIcons.General.InspectionsOK
                        verifyLabel?.text = message("settings.verify.valid")
                    }
                    catch (e: Exception) {
                        verifyLabel?.icon = AllIcons.General.InspectionsError
                        verifyLabel?.text = message("settings.verify.invalid", e.cause?.localizedMessage.orEmpty())
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

    private fun initForceHttpsCheckBox() {
        val projectSettings = project.service<ProjectSettings>()
        forceHttpsCheckBox?.addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> projectSettings.forceHttps = true
                ItemEvent.DESELECTED -> projectSettings.forceHttps = false
            }
        }
        forceHttpsCB = projectSettings.forceHttps
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

    var gitlabUrlCB: String?
        get() {
            return gitlabUrlComboBox?.selectedItem as String?
        }
        set(newGitlabUrl) {
            gitlabUrlComboBox?.selectedItem = newGitlabUrl
        }

    var gitlabRemoteTF: String?
        get() {
            return gitlabRemoteTextField?.text
        }
        set(newRemote) {
            gitlabRemoteTextField?.text = newRemote
        }

    var forceHttpsCB: Boolean
        get() {
            return forceHttpsCheckBox?.isSelected?: false
        }
        set(selected) {
            forceHttpsCheckBox?.isSelected = selected
        }
}

