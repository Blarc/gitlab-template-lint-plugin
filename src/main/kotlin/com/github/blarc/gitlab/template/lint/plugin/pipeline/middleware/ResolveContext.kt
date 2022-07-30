package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabFactory
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

@Service
class ResolveContext : Middleware {
    override val priority = 5
    private var showGitlabTokenNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val repository = locateRepository(pass) ?: return null
        val remote = locateRemote(pass, repository) ?: return null
        val url = remote.httpUrl ?: return null
        val gitlabToken = getGitlabToken(pass) ?: return null

        pass.gitlab = GitlabFactory.getInstance(pass.project)!!.getGitLab(url, gitlabToken)
        pass.repository = repository
        pass.remote = remote

        return next()
    }

    private fun locateRepository(pass: Pass): GitRepository? {
        val repository = GitRepositoryManager.getInstance(pass.project).getRepositoryForFile(pass.file.virtualFile)

        repository ?: sendNotification(Notification.repositoryNotFound(), pass.project)

        return repository
    }

    private fun locateRemote(pass: Pass, repository: GitRepository): GitRemote? {
        val remote = repository.locateRemote(pass.project.service<ProjectSettings>().remote)

        remote ?: sendNotification(Notification.remoteNotFound(), pass.project)

        return remote
    }

    private fun getGitlabToken(pass: Pass) : String? {
        val gitlabToken = AppSettings.instance?.gitlabToken

        if (gitlabToken.isNullOrEmpty() && showGitlabTokenNotification) {
            sendNotification(Notification.gitlabTokenNotSet(pass.project), pass.project)
            showGitlabTokenNotification = false
        }
        else if (!gitlabToken.isNullOrEmpty()) {
            showGitlabTokenNotification = true
        }

        return gitlabToken
    }
}
