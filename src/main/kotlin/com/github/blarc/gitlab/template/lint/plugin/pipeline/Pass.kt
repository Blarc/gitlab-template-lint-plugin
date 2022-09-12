package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import git4idea.repo.GitRepository

class Pass(val project: Project, val file: PsiFile) {
    var repository: GitRepository? = null
    var remoteUrl: String? = null
    var remoteId: Long? = null
    var gitlabUrl: String? = null
    var gitlabToken: String? = null

    fun gitlabUrlOrThrow() = gitlabUrl ?: throw IllegalStateException("Gitlab url not set")
    fun gitlabTokenOrThrow() = gitlabToken ?: throw IllegalStateException("Gitlab token not set")
    fun repositoryOrThrow() = repository ?: throw IllegalStateException("Repository not set")
    fun remoteOrThrow() = remoteUrl ?: throw IllegalStateException("Remote not set")
    fun remoteIdOrThrow() = remoteId ?: throw IllegalStateException("Remote ID not set")
}
