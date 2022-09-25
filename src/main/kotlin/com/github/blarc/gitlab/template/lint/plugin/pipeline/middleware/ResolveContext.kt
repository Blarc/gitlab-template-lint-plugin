package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.toHttps
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.Remote
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

@Service
class ResolveContext : Middleware {
    override val priority = 1
    private var showRepositoryNotification = true
    private var showRemoteNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {

        val repository = locateRepository(pass) ?: return null
        val remote = locateRemote(pass, repository) ?: return null

        pass.repository = repository

        val settings = pass.project.service<ProjectSettings>()
        val remotesMap = AppSettings.instance?.remotes
        if (settings.forceHttps) {
            pass.remoteUrl = remote.httpUrl?.toHttps().toString()

            if (remotesMap?.containsKey(pass.remoteUrl) == true) {
                pass.gitlabUrl = remotesMap[pass.remoteUrl]?.gitlabUrl
            }
            else {
                pass.gitlabUrl = remote.gitlabUrl?.toHttps().toString()
                remotesMap?.set(pass.remoteUrl!!, Remote(pass.remoteUrl!!, pass.gitlabUrl, null))
            }
        }
        else {
            pass.remoteUrl = remote.httpUrl?.toString()
            if (remotesMap?.containsKey(pass.remoteUrl) == true) {
                pass.gitlabUrl = remotesMap[pass.remoteUrl]?.gitlabUrl
            }
            else {
                pass.gitlabUrl = remote.gitlabUrl?.toString()
                remotesMap?.set(pass.remoteUrl!!, Remote(pass.remoteUrl!!, pass.gitlabUrl, null))
            }
        }

        return next()
    }

    private fun locateRepository(pass: Pass): GitRepository? {
        val repository = GitRepositoryManager.getInstance(pass.project).getRepositoryForFile(pass.file.virtualFile)

        showRepositoryNotification = if (repository == null) {
            sendNotification(Notification.repositoryNotFound(), pass.project)
            false
        } else {
            true
        }

        return repository
    }

    private fun locateRemote(pass: Pass, repository: GitRepository): GitRemote? {
        val remote = repository.locateRemote(pass.project.service<ProjectSettings>().remote)

        showRemoteNotification = if (remote == null) {
            sendNotification(Notification.remoteNotFound(), pass.project)
            false
        } else {
            true
        }

        return remote
    }
}
