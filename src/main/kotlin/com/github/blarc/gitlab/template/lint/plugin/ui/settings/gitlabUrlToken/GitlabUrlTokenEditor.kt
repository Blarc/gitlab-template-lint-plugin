package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField

class GitlabUrlTokenEditor(val project: Project, title: String, gitlabUrlToken: GitlabUrlToken?) : DialogWrapper(true) {
    var basePanel: JPanel? = null
    private var verifyButton: JButton? = null
    private var verifyLabel: JBLabel? = null
    private var gitlabUrlTextField: JBTextField? = null
    private var gitlabTokenPasswordField: JPasswordField? = null

    init {
        this.title = title
        gitlabUrlTextField?.text = gitlabUrlToken?.gitlabUrl
        gitlabTokenPasswordField?.text = gitlabUrlToken?.gitlabToken
        initVerifyButton()
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return basePanel
    }

    fun getGitlabUrl(): String? {
        return gitlabUrlTextField?.text
    }

    fun getGitlabToken(): String? {
        return gitlabTokenPasswordField?.password?.let { String(it) }
    }

    private fun initVerifyButton() {
        verifyButton?.addActionListener {
            runBackgroundableTask(GitlabLintBundle.message("settings.verify.running")) {
                val gitlabUrl = getGitlabUrl()
                if (gitlabUrl.isNullOrEmpty()) {
                    verifyLabel?.icon = AllIcons.General.InspectionsError
                    verifyLabel?.text = GitlabLintBundle.message("settings.verify.gitlab-url-not-set")
                }
                else {
                    verifyLabel?.icon = AllIcons.General.InlineRefreshHover
                    verifyLabel?.text = GitlabLintBundle.message("settings.verify.running")
                    try {
                        project.service<Gitlab>().getVersion(gitlabUrl, getGitlabToken().orEmpty()).get()
                        verifyLabel?.icon = AllIcons.General.InspectionsOK
                        verifyLabel?.text = GitlabLintBundle.message("settings.verify.valid")
                    }
                    catch (e: Exception) {
                        verifyLabel?.icon = AllIcons.General.InspectionsError
                        verifyLabel?.text = GitlabLintBundle.message("settings.verify.invalid", e.cause?.localizedMessage.orEmpty())
                    }
                }
            }
        }
    }

}
