package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepositoryManager
import java.net.URI

@Service
class GitlabDetector(val project: Project) {
    fun detect(file: VirtualFile) : URI? {
        val settings = project.service<ProjectSettings>()

        val repository = GitRepositoryManager.getInstance(project).getRepositoryForFile(file) ?: return null
        val remote = repository.locateRemote(settings.remote) ?: return null

        return remote.gitlabUrl
    }
}
