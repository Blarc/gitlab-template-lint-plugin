package com.github.blarc.gitlab.template.lint.plugin.ui.settings;

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import javax.swing.JComponent
import javax.swing.JPanel

class RemoteEditor(title: String, remoteUrl: String?, remoteId: Long?) : DialogWrapper(true)
{
    var basePanel: JPanel? = null
    private var urlTextField: JBTextField? = null
    private var idTextField: JBTextField? = null

    init {
        this.title = title
        urlTextField?.text = remoteUrl
        idTextField?.text = remoteId?.toString()
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return basePanel
    }

    fun getRemoteUrl(): String? {
        return urlTextField?.text
    }

    fun getRemoteId(): Long? {
        return idTextField?.text?.toLong()
    }

}
