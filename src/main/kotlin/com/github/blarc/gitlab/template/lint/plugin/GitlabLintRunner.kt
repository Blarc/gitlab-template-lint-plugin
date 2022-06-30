package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import git4idea.repo.GitRepository
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.CompletableFuture


@Service
class GitlabLintRunner(private val project: Project) {

    fun run(content: String, filePath: String): GitlabLintResponse? {

        val repositoryManager = GitUtil.getRepositoryManager(project)
        val repository = DvcsUtil.guessCurrentRepositoryQuick(project, repositoryManager, filePath)

        if (repository != null) {
            var result: GitlabLintResponse? = null
            try {

                val remoteUrl = getRemoteUrl(repository)
                val remoteUrlString = remoteUrl.toString()
                val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${remoteUrl?.scheme}://${remoteUrl?.host}")

                getProjectId(remoteUrlString, gitlab).thenAccept { projectId ->

                    // Cache project id
                    val remotesMap = AppSettingsState.instance.remotesMap
                    if (!remotesMap.containsKey(remoteUrlString)) {
                        remotesMap[remoteUrlString] = projectId
                    }

                    val branch = repository.currentBranch!!.name
                    gitlab.lintContent(content, projectId, branch).thenAccept { gitlabLintResponse ->
                        result = gitlabLintResponse
                    }.get()

                }.get()
            }
            catch (e: Exception) {
                GitlabLintUtils.createNotification(project, e.localizedMessage?: "Unknown exception!")
            }

            return result
        }

        GitlabLintUtils.createNotification(project, "File is not part of a Gitlab repository.")
        return null
    }

    private fun getProjectId(remoteUrl: String, gitlab: Gitlab) : CompletableFuture<Long> {
        val remotesMap = AppSettingsState.instance.remotesMap
        if (remotesMap.containsKey(remoteUrl)) {
            return CompletableFuture.completedFuture(remotesMap[remoteUrl])
        }
        return gitlab.searchProjectId(remoteUrl.removeSuffix(".git"))
    }

    private fun getRemoteUrl(repository: GitRepository): HttpUrl? {
        var urlString = repository.remotes.first().firstUrl

        // Example: git@github.com:facebook/react.git
        if (urlString!!.startsWith("git@")) {
            urlString = urlString.removePrefix("git@")
            urlString = urlString.replaceFirst(":", "/")
            urlString = "https://${urlString}"
        }
        // Example: git://github.com/facebook/react.git#gh-pages
        else if (urlString.startsWith("git:")) {
            urlString = urlString.removePrefix("git")
            urlString = "https${urlString}"
        }

        return urlString.toHttpUrlOrNull()
    }
}
