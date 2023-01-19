package com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote;

import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel

class RemoteEditor(project: Project, title: String, remote: Remote?) : DialogWrapper(true)
{
    var basePanel: JPanel? = null
    private var remoteUrlTextField: JBTextField? = null
    private var gitlabUrlComboBox: JComboBox<String>? = null
    private var remoteIdTextField: JBTextField? = null

    init {
        this.title = title
        remoteUrlTextField?.text = remote?.remoteUrl

        val projectSettings = project.service<ProjectSettings>()
        gitlabUrlComboBox?.model = DefaultComboBoxModel(projectSettings.gitlabUrls.toTypedArray())
        remoteIdTextField?.text = remote?.remoteId?.toString()
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return basePanel
    }

    fun getRemoteUrl(): String? {
        return remoteUrlTextField?.text
    }

    fun getGitlabUrl(): String? {
        return gitlabUrlComboBox?.selectedItem?.toString()
    }

    fun getRemoteId(): Long? {
        return remoteIdTextField?.text?.ifEmpty { null }?.toLong()
    }

}
