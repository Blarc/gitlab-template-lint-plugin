package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabDetector
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
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
        val gitlabUrl = resolveGitlabUrl(pass) ?: return null


        pass.repository = repository
        pass.remote = remote
        pass.gitlabUrl = gitlabUrl

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

    private fun resolveGitlabUrl(pass: Pass): String? {
        return pass.project.service<ProjectSettings>().gitlabUrl ?: pass.project.service<GitlabDetector>().detect(pass.file.virtualFile)?.toString()
    }
}
