package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.toHttps
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.pipeline.PipelineStatusEnum
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.Remote
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.net.URI

@Service
class ResolveContext : Middleware {
    override val priority = 1
    private var showRepositoryNotification = true
    private var showRemoteNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabObject?, PipelineStatusEnum>?): Pair<GitlabObject?, PipelineStatusEnum>? {

        val repository = locateRepository(pass) ?: return null
        pass.repository = repository

        val remote = locateRemote(pass, repository) ?: return null
        val remotesMap = AppSettings.instance.remotes

        var remoteUrl = remote.httpUrl ?: return null
        if (pass is Pass.ResolveInclude && pass.properties.containsKey("project")) {
            remoteUrl = URI(remoteUrl.scheme + "://" + remoteUrl.host + "/" + pass.properties["project"])
        }

        val settings = pass.project.service<ProjectSettings>()
        if (settings.forceHttps) {
            pass.remoteUrl = remoteUrl.toHttps().toString()
        } else {
            pass.remoteUrl = remoteUrl.toString()
        }

        if (remotesMap.containsKey(pass.remoteUrl)) {
            pass.gitlabUrl = remotesMap[pass.remoteUrl]?.gitlabUrl
        }
        else if (settings.forceHttps) {
            pass.gitlabUrl = remote.gitlabUrl?.toHttps().toString()
            remotesMap[pass.remoteUrl!!] = Remote(pass.remoteUrl!!, pass.gitlabUrl, null)
        }
        else {
            pass.gitlabUrl = remote.gitlabUrl?.toString()
            remotesMap[pass.remoteUrl!!] = Remote(pass.remoteUrl!!, pass.gitlabUrl, null)
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
