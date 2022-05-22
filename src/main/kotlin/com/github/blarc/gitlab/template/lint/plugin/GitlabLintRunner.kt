package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.intellij.dvcs.DvcsUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
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
            val url = repository.remotes.first().urls[0].toHttpUrlOrNull()
            val projectName = repository.project.name
            val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${url?.scheme}://${url?.host}")

            var result: GitlabLintResponse? = null
            gitlab.searchProject(projectName).thenAccept {
                val projectId = it[0].id
                gitlab.lintContent(content, projectId).thenAccept { gitlabLintResponse ->
                    result = gitlabLintResponse
                }.get()
            }.get()

            return result
        }

        NotificationGroupManager.getInstance()
            .getNotificationGroup("GitlabLint")
            .createNotification("Gitlab Lint", "Project is not a gitlab repo.", NotificationType.ERROR)
            .notify(project)

        return null
    }
}
