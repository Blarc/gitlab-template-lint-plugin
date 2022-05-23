package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


@Service
class GitlabLintRunner(private val project: Project) {

    fun run(content: String, filePath: String): GitlabLintResponse? {

        val repositoryManager = GitUtil.getRepositoryManager(project)
        val repository = DvcsUtil.guessCurrentRepositoryQuick(project, repositoryManager, filePath)

        if (repository != null) {
            var result: GitlabLintResponse? = null
            try {

                val url = repository.remotes.first().firstUrl?.toHttpUrlOrNull()
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

        GitlabLintUtils.createNotification(project, "Project is not a gitlab repository.")
        return null
    }
}
