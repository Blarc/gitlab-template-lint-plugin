package com.github.blarc.gitlab.template.lint.plugin.ui.settings;

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import javax.swing.JComponent
import javax.swing.JPanel

class RemoteEditor(title: String, remote: Remote?) : DialogWrapper(true)
{
    var basePanel: JPanel? = null
    private var remoteUrlTextField: JBTextField? = null
    private var gitlabUrlTextField: JBTextField? = null
    private var remoteIdTextField: JBTextField? = null

    init {
        this.title = title
        remoteUrlTextField?.text = remote?.remoteUrl
        gitlabUrlTextField?.text = remote?.gitlabUrl
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
        return gitlabUrlTextField?.text
    }

    fun getRemoteId(): Long? {
        return remoteIdTextField?.text?.toLong()
    }

}
