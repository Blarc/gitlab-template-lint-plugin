package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

@Service
class ResolveContext : Middleware {
    override val priority = 5

    override fun invoke(pass: Pass, next: () -> GitlabLintResponse?): GitlabLintResponse? {
        val repository = locateRepository(pass) ?: return null
        val remote = locateRemote(pass, repository) ?: return null

        pass.gitlab = GitLabFactory.getInstance(pass.project)!!.getGitLab("${remote.httpUrl?.scheme}://${remote.httpUrl?.host}")
        pass.repository = repository
        pass.remote = remote

        return next()
    }

    private fun locateRepository(pass: Pass): GitRepository? {
        val repository = GitRepositoryManager.getInstance(pass.project).getRepositoryForFile(pass.file.virtualFile)

//        repository ?: sendNotification(Notification.repositoryNotFound(), pass.project)

        return repository
    }

    private fun locateRemote(pass: Pass, repository: GitRepository): GitRemote? {
        val remote = repository.locateRemote(pass.project.service<ProjectSettings>().remote)

//        remote ?: sendNotification(Notification.remoteNotFound(), pass.project)

        return remote
    }
}
