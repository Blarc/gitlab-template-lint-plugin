package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository

class Pass(val project: Project, val file: PsiFile) {
    var gitlab: Gitlab? = null
    var repository: GitRepository? = null
    var remote: GitRemote? = null
    var remoteId: Long? = null

    fun gitlabOrThrow() = gitlab ?: throw IllegalStateException("Gitlab not set")
    fun repositoryOrThrow() = repository ?: throw IllegalStateException("Repository not set")
    fun remoteOrThrow() = remote ?: throw IllegalStateException("Remote not set")
    fun remoteIdOrThrow() = remoteId ?: throw IllegalStateException("Remote ID not set")
}
