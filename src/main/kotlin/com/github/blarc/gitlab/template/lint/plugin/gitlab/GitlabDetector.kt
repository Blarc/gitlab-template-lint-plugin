package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import git4idea.repo.GitRepositoryManager
import java.net.URI

@Service
class GitlabDetector(val project: Project) {
    fun detect() : URI? {
        val settings = project.service<ProjectSettings>()

        val projectDirectory = project.guessProjectDir() ?: return null
        val repository = GitRepositoryManager.getInstance(project).getRepositoryForFile(projectDirectory) ?: return null
        val remote = repository.locateRemote(settings.remote) ?: return null

        return remote.gitlabUrl
    }
}
