package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import git4idea.repo.GitRepository
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


@Service
class GitlabLintRunner(private val project: Project) {

    fun run(content: String, filePath: String): GitlabLintResponse? {

        val repositoryManager = GitUtil.getRepositoryManager(project)
        val repository = DvcsUtil.guessCurrentRepositoryQuick(project, repositoryManager, filePath)

        if (repository != null) {
            var result: GitlabLintResponse? = null
            try {

                val url = getRemoteUrl(repository)
                val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${url?.scheme}://${url?.host}")

                gitlab.searchProject(url.toString().removeSuffix(".git")).thenAccept {
                    val projectId = it.id
                    val branch = repository.currentBranch!!.name
                    gitlab.lintContent(content, projectId, branch).thenAccept { gitlabLintResponse ->
                        result = gitlabLintResponse
                    }.get()
                }.get()
            }
            catch (e: Exception) {
                GitlabLintUtils.createNotification(project, e.localizedMessage)
            }

            return result
        }

        GitlabLintUtils.createNotification(project, "Project is not a Gitlab repository.")
        return null
    }

    private fun getRemoteUrl(repository: GitRepository): HttpUrl? {
        var urlString = repository.remotes.first().firstUrl

        // git@github.com:facebook/react.git
        if (urlString!!.startsWith("git@")) {
            urlString = urlString.removePrefix("git@")
            urlString = urlString.replaceFirst(":", "/")
            urlString = "https://${urlString}"
        }
        // git://github.com/facebook/react.git#gh-pages
        else if (urlString.startsWith("git:")) {
            urlString = urlString.removePrefix("git")
            urlString = "https${urlString}"
        }

        return urlString.toHttpUrlOrNull()
    }
}
