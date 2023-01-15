package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken.GitlabUrlTokenTable
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken.GitlabUrlTokenTableModel
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.RemotesTable
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.RemotesTableModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.BrowserLink
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JPanel

class SettingsForm(val project: Project) {
    var basePanel: JPanel? = null
    private var lintFrequencyComboBox: JComboBox<String>? = null
    private var gitlabRemoteTextField: JBTextField? = null
    private var remotesTablePanel: JPanel? = null
    private var gitlabUrlTokenTablePanel: JPanel? = null
    private var reportBugLink: BrowserLink? = null
    private var forceHttpsCheckBox: JCheckBox? = null
    private var showMergedPreviewCheckBox: JCheckBox? = null
    private var runLintOnFileChangeCheckBox: JCheckBox? = null

    val gitlabRemotesTable = RemotesTable(project, RemotesTableModel())
    val gitlabUrlTokenTable = GitlabUrlTokenTable(project, GitlabUrlTokenTableModel())

    init {
        initLintFrequencyComboBox()
        initGitlabUrlTokenTable()
        initRemoteField()
        initRemotesTable()
        initForceHttpsCheckBox()
        initShowMergedPreviewCheckBox()
    }

    private fun initLintFrequencyComboBox() {
        lintFrequencyComboBox?.model =
            DefaultComboBoxModel(LintFrequencyEnum.values().map { it.message }.toTypedArray())

        lintFrequencyComboBox?.selectedItem = AppSettings.instance?.lintFrequency?.message
    }

    private fun initRemoteField() {
        val gitlabRemote = project.service<ProjectSettings>().remote
        gitlabRemoteTextField?.text = gitlabRemote
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

    private fun initGitlabUrlTokenTable() {
        gitlabUrlTokenTablePanel!!.add(
            ToolbarDecorator.createDecorator(gitlabUrlTokenTable)
                .setAddAction { gitlabUrlTokenTable.addGitlabUrlToken() }
                .setRemoveAction { gitlabUrlTokenTable.removeSelectedGitlabUrlTokens() }
                .setEditAction { gitlabUrlTokenTable.editGitlabUrlToken() }
                .createPanel(),
            BorderLayout.CENTER
        )

        object : DoubleClickListener() {
            override fun onDoubleClick(e: MouseEvent): Boolean {
                return gitlabUrlTokenTable.editGitlabUrlToken()
            }
        }.installOn(gitlabUrlTokenTable)

    }

    private fun initForceHttpsCheckBox() {
        forceHttpsCheckBox?.addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> forceHttpsCB = true
                ItemEvent.DESELECTED -> forceHttpsCB = false
            }
        }
    }

    private fun initShowMergedPreviewCheckBox() {
        showMergedPreviewCheckBox?.addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> showMergedPreviewCB = true
                ItemEvent.DESELECTED -> showMergedPreviewCB= false
            }
        }
    }

    private fun initRunLintOnFileChangeCheckBox() {
        runLintOnFileChangeCheckBox?.addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> runLintOnFileChangeCB = true
                ItemEvent.DESELECTED -> runLintOnFileChangeCB = false
            }
        }
    }

    fun createUIComponents() {
        reportBugLink = BrowserLink(message("actions.report-bug.title"), GitlabLintBundle.URL_BUG_REPORT.toString())
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
            return forceHttpsCheckBox?.isSelected ?: false
        }
        set(selected) {
            forceHttpsCheckBox?.isSelected = selected
        }

    var lintFrequency: LintFrequencyEnum
        get() { val lintFrequencyString = (lintFrequencyComboBox?.selectedItem as String)
            return LintFrequencyEnum.values().find {
                    lintFrequencyEnum -> lintFrequencyEnum.message == lintFrequencyString
            }!!
        }
        set (selected) {
            lintFrequencyComboBox?.selectedItem = selected
        }

    var showMergedPreviewCB: Boolean
        get() {
            return showMergedPreviewCheckBox?.isSelected ?: false
        }
        set(selected) {
            showMergedPreviewCheckBox?.isSelected = selected
        }

    var runLintOnFileChangeCB: Boolean
        get() {
            return runLintOnFileChangeCheckBox?.isSelected ?: false
        }
        set(selected) {
            runLintOnFileChangeCheckBox?.isSelected = selected
        }
}

